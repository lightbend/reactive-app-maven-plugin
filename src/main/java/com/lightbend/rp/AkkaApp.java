package com.lightbend.rp;

import org.apache.maven.project.MavenProject;

public class AkkaApp implements ReactiveApp {
    private BasicApp basic;

    public AkkaApp(Settings settings, Labels labels, Endpoints endpoints) {
        basic = new BasicApp(settings, labels, endpoints);
    }

    @Override
    public void apply(MavenProject project) {
        basic.apply(project);
    }
}
