package com.lightbend.rp;

import org.apache.maven.project.MavenProject;

public interface ReactiveApp {
    /**
     * Applies necessary modifications to the given maven project to make run in reactive ecosystem.
     */
    void apply(MavenProject project);
}
