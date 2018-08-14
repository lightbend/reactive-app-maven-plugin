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

        final String name;
        final String protocol;
        final ArrayList<Ingress> ingresses = new ArrayList<>();

        Endpoint(final String name, final String protocol) {
            this.name = name;
            this.protocol = protocol;
        }

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

    public Endpoint addEndpoint(final String name, final String protocol) {
        final Endpoint e = new Endpoint(name, protocol);
        endpoints.add(e);
        return e;
    }

    public void writeToLabels(Labels labels) {
        for (int i = 0; i < endpoints.size(); ++i) {
            endpoints.get(i).writeToLabels("endpoints." + Integer.toString(i) + ".", labels);
        }
    }
}
