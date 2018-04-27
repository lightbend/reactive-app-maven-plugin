package com.lightbend.rp;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

@Mojo( name = "inject-deps", defaultPhase = LifecyclePhase.PROCESS_SOURCES,
        requiresDependencyResolution = ResolutionScope.NONE)
public class InjectDepsMojo extends AbstractMojo {
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject mavenProject;

    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException {
        Log log = getLog();

        Dependency dep = new Dependency();
        dep.setArtifactId("id-of-my-artifact");
        dep.setGroupId("com.my.name.group");
        dep.setScope("compile");
        dep.setVersion("2.3.0-SNAPSHOT");

        mavenProject.getDependencyManagement().addDependency(dep);
    }
}
