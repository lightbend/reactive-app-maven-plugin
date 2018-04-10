package com.lightbend.rp;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo( name = "build", defaultPhase = LifecyclePhase.INSTALL )
public class BuildMojo extends AbstractMojo {
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    Plugin getThisPlugin() {
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

        Settings settings = new Settings(pluginConf);
        Labels labels = new Labels();

        AppType type = AppTypeDetector.detect(mavenProject);
        settings.appType = type;
        log.info("App type: " + type.toString());
        settings.appName = mavenProject.getName();
        log.info("App name: " + settings.appName);
        settings.appVersion = mavenProject.getVersion();
        log.info("App version: " + settings.appVersion);

        ReactiveApp analyser = null;
        switch(type) {
            case Basic:
                analyser = new BasicApp();
                break;
            case Akka:
                analyser = new AkkaApp();
                break;
            case Play:
                analyser = new PlayApp();
                break;
            case Lagom:
                analyser = new LagomApp();
                break;
            default:
                log.error("Unknown app type " + type.toString());
        }

        if(analyser == null)
            throw new MojoExecutionException("Unknown app type");

        analyser.apply(mavenProject, settings, labels);

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
                                                        element("JAVA_MAIN_CLASS", "${mainClass}"),
                                                        element("JAVA_APP_JAR", "${project.name}-${project.version}.jar")
                                                ),
                                                element("cmd", "deployments/run-java.sh")
                                        )
                                )
                        )
                );

        labels.writeToConf(conf);

        executeMojo(
                plugin(groupId("io.fabric8"), artifactId("docker-maven-plugin"), version("0.23.0")),
                goal("build"),
                conf,
                executionEnvironment(mavenProject, mavenSession, pluginManager)
        );
    }
}
