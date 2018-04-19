package com.lightbend.rp;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LagomApp implements ReactiveApp {
    BasicApp basic = new BasicApp();

    @Override
    public void apply(MavenProject project, Settings settings, Labels labels) throws DependencyResolutionRequiredException {
        basic.apply(project, settings, labels);

        List<String> l = project.getCompileClasspathElements();
        //System.out.println("Classpath: " + l.toString());
    }
}
