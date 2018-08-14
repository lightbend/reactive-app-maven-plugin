package com.lightbend.rp;

import java.util.ArrayList;

public class Endpoints {
    class Endpoint {
        class Ingress {
            String type;
            ArrayList<String> ports = new ArrayList<>();
            ArrayList<String> paths = new ArrayList<>();

            private void writeToLabels(String prefix, Labels labels) {
                labels.add(prefix + "type", type);
                for (int i = 0; i < ports.size(); ++i) {
                    labels.add(prefix + "ingress-ports." + Integer.toString(i), ports.get(i));
                }
                for (int i = 0; i < paths.size(); ++i) {
                    labels.add(prefix + "paths." + Integer.toString(i), paths.get(i));
                }
            }
        }

        String name;
        String protocol;
        ArrayList<Ingress> ingresses = new ArrayList<>();

        public Ingress addIngress() {
            Ingress n = new Ingress();
            ingresses.add(n);
            return n;
        }

        private void writeToLabels(String prefix, Labels labels) {
            labels.add(prefix + "name", name);
            labels.add(prefix + "protocol", protocol);
            for (int i = 0; i < ingresses.size(); ++i) {
                ingresses.get(i).writeToLabels(prefix + "ingress." + Integer.toString(i) + ".", labels);
            }
        }
    }

    private ArrayList<Endpoint> endpoints = new ArrayList<>();

    public Endpoint addEndpoint() {
        Endpoint n = new Endpoint();
        endpoints.add(n);
        return n;
    }

    public void addEndpoint(final String name, final String protocol) {
        final Endpoint e = addEndpoint();
        e.name = name;
        e.protocol = protocol;
    }

    public void writeToLabels(Labels labels) {
        for (int i = 0; i < endpoints.size(); ++i) {
            endpoints.get(i).writeToLabels("endpoints." + Integer.toString(i) + ".", labels);
        }
    }
}
