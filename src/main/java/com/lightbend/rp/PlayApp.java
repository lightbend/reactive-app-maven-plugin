package com.lightbend.rp;

import org.apache.maven.project.MavenProject;

public class PlayApp implements ReactiveApp {
    private BasicApp basic;

    public PlayApp(Settings settings, Labels labels, Endpoints endpoints, Applications applications) {
        basic = new BasicApp(settings, labels, endpoints, applications);
    }

    @Override
    public void apply(MavenProject project) {
        basic.apply(project);
    }
}
