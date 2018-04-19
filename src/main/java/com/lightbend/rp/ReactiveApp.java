package com.lightbend.rp;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

public interface ReactiveApp {
    /**
     * Applies necessary modifications to the given maven project to make it run in reactive ecosystem.
     */
    void apply(MavenProject project, Settings settings, Labels labels) throws DependencyResolutionRequiredException;
}
