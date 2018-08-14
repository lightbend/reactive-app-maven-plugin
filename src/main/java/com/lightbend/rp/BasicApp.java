package com.lightbend.rp;

import org.apache.maven.project.MavenProject;

public class BasicApp implements ReactiveApp {
    private Settings settings;
    private Labels labels;
    private Endpoints endpoints;
    private Applications applications;

    public BasicApp(Settings settings, Labels labels, Endpoints endpoints, Applications applications) {
        this.settings = settings;
        this.labels = labels;
        this.endpoints = endpoints;
        this.applications = applications;
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

    public Applications getApplications() {
        return applications;
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

        // HTTP ingress
        if(!settings.httpIngressPaths.isEmpty() || !settings.httpIngressPorts.isEmpty()) {
            Endpoints.Endpoint e = endpoints.addEndpoint("http", "http");
            Endpoints.Endpoint.Ingress i = e.addIngress();
            i.type = "http";
            i.paths = settings.httpIngressPaths;
            i.ports = settings.httpIngressPorts;
        }

        // Additional modules
        if(settings.enableCommon)
            labels.add("modules.common.enabled", "true");
        if(settings.enablePlayHttpBinding)
            labels.add("modules.play-http-binding.enabled", "true");
        if(settings.enableServiceDiscovery)
            labels.add("modules.service-discovery.enabled", "true");
        if(settings.enableAkkaClusterBootstrap) {
            labels.add("modules.akka-cluster-bootstrap.enabled", "true");
            labels.add("modules.status.enabled", "true");
            labels.add("modules.akka-management.enabled", "true");
            endpoints.addEndpoint(settings.akkaClusterBootstrapEndpointName, "tcp");
            endpoints.addEndpoint(settings.akkaManagementEndpointName, "tcp");
        }
        if(settings.enableStatus) {
            labels.add("modules.status.enabled", "true");
        }
    }
}
