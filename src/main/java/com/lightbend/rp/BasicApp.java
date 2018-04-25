package com.lightbend.rp;

import org.apache.maven.project.MavenProject;

public class BasicApp implements ReactiveApp {
    private Settings settings;
    private Labels labels;
    private Endpoints endpoints;

    public BasicApp(Settings settings, Labels labels, Endpoints endpoints) {
        this.settings = settings;
        this.labels = labels;
        this.endpoints = endpoints;
    }

    public Settings getSettings() {
        return settings;
    }

    public Labels getLabels() {
        return labels;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    @Override
    public void apply(MavenProject project) {
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
