package com.lightbend.rp;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo( name = "docker", defaultPhase = LifecyclePhase.INSTALL )
public class ReactiveAppMojo extends AbstractMojo {
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException {
        AppType type = AppTypeDetector.detect(mavenProject);
        getLog().info("App type: " + type.toString());

        executeMojo(
                plugin(groupId("io.fabric8"), artifactId("docker-maven-plugin"), version("0.23.0")),
                goal("build"),
                configuration(
                        element("images",
                                element("image",
                                        element("name", "${project.name}:${project.version}"),
                                        element("alias", "rp-hello"),
                                        element("build",
                                                element("from", "openjdk:latest"),
                                                element("assembly",
                                                        element("descriptorRef", "artifact")
                                                        ),
                                                element("cmd", "java -jar maven/${project.name}-${project.version}.jar")
                                        )
                                )
                        )
                ),
                executionEnvironment(mavenProject, mavenSession, pluginManager)
        );
    }
}
