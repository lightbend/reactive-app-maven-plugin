package com.lightbend.rp;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo( name = "build", defaultPhase = LifecyclePhase.INSTALL,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class BuildMojo extends AbstractMojo {
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    private Plugin getThisPlugin() {
        for(Plugin plugin : mavenProject.getBuildPlugins()) {
            if(plugin.getArtifactId().equals("reactive-app-maven-plugin")) {
                return plugin;
            }
        }
        return null;
    }

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        Xpp3Dom pluginConf = (Xpp3Dom)getThisPlugin().getConfiguration();

        Settings settings = new Settings();
        Labels labels = new Labels();
        Endpoints endpoints = new Endpoints();
        Applications applications = new Applications();
        ReactiveLibChecker checker = new ReactiveLibChecker(mavenProject, settings, log);

        labels.add("reactive-maven-app-version", getThisPlugin().getVersion());

        AppType type = AppTypeDetector.detect(mavenProject);
        settings.appType = type;
        log.info("App type: " + type.toString());
        settings.appName = mavenProject.getArtifactId();
        log.info("App name: " + settings.appName);
        settings.appVersion = mavenProject.getVersion();
        log.info("App version: " + settings.appVersion);

        ReactiveApp analyser = null;
        switch(type) {
            case Basic:
                analyser = new BasicApp(settings, labels, endpoints, applications);
                break;
            case Akka:
                analyser = new AkkaApp(settings, labels, endpoints, applications);
                break;
            case Play:
                analyser = new PlayApp(settings, labels, endpoints, applications);
                break;
            case Lagom:
                analyser = new LagomApp(settings, labels, endpoints, applications);
                break;
            default:
                log.error("Unknown app type " + type.toString());
        }

        if(analyser == null)
            throw new MojoExecutionException("Unknown app type");

        settings.read(pluginConf);

        checker.preApplyCheck();

        analyser.apply(mavenProject);

        // Add default application if analysers didn't create any
        if(applications.applications.isEmpty()) {
            Applications.Application a = applications.addApplication();
            a.name = "default";
            a.arguments.add("deployments/run-java.sh");
        }

        if(settings.mainClass == null || settings.mainClass.isEmpty()) {
            throw new MojoExecutionException("main class was not defined; " +
                    "do that by adding <mainClass>com.app.Main</mainClass> to plugin configuration");
        }

        checker.check();

        Xpp3Dom conf = configuration(
                        element("images",
                                element("image",
                                        element("name", "${project.name}:${project.version}"),
                                        element("alias", "rp-${project.name}"),
                                        element("build",
                                                element("from", "fabric8/java-alpine-openjdk8-jre"),
                                                element("assembly",
                                                        element("targetDir", "/deployments"),
                                                        element("descriptorRef", "artifact-with-dependencies")
                                                        ),
                                                element("env",
                                                        element("AB_OFF", "1"),
                                                        element("JAVA_MAIN_CLASS", settings.mainClass),
                                                        element("JAVA_APP_JAR", "${project.name}-${project.version}.jar")
                                                )
                                        )
                                )
                        )
                );

        applications.writeToLabels(labels);
        endpoints.writeToLabels(labels);
        labels.writeToConf(conf);

        executeMojo(
                plugin(groupId("io.fabric8"), artifactId("docker-maven-plugin"), version("0.23.0")),
                goal("build"),
                conf,
                executionEnvironment(mavenProject, mavenSession, pluginManager)
        );
    }
}
