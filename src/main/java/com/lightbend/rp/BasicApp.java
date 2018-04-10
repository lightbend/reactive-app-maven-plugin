package com.lightbend.rp;

import org.apache.maven.project.MavenProject;

public class BasicApp implements ReactiveApp {

    @Override
    public void apply(MavenProject project, Settings settings, Labels labels) {
        // Required labels
        labels.add("app-name", settings.appName);
        labels.add("app-version", settings.appVersion);
        labels.add("app-type", settings.appType.toString().toLowerCase());

        // Resource quotas
        if(settings.cpu != null)
            labels.add("cpu", settings.cpu.toString());
        if(settings.memory != null)
            labels.add("memory", settings.memory.toString());
        if(settings.diskSpace != null)
            labels.add("disk-space", settings.diskSpace.toString());
    }
}
