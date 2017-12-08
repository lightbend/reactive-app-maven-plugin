package com.lightbend.rp;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo( name = "docker", defaultPhase = LifecyclePhase.INSTALL )
public class ReactiveAppMojo extends AbstractMojo {
    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException {
        executeMojo(
                plugin(groupId("io.fabric8"), artifactId("docker-maven-plugin"), version("0.23.0")),
                goal("build"), configuration(),
                executionEnvironment(mavenProject, mavenSession, pluginManager)
        );
    }
}
