package com.lightbend.rp;

import org.apache.maven.project.MavenProject;

public class LagomApp implements ReactiveApp {
    BasicApp basic = new BasicApp();

    @Override
    public void apply(MavenProject project, Settings settings, Labels labels) {
        basic.apply(project, settings, labels);
    }
}
