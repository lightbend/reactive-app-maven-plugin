package com.lightbend.rp;

import java.util.ArrayList;

public class Applications {
    class Application {
        String name;
        ArrayList<String> arguments = new ArrayList<>();

        private void writeToLabels(String prefix, Labels labels) {
            labels.add(prefix + "name", name);
            for(int i = 0; i < arguments.size(); ++i) {
                labels.add(prefix + "arguments." + Integer.toString(i), arguments.get(i));
            }
        }
    }

    ArrayList<Application> applications = new ArrayList<>();

    public Application addApplication() {
        Application n = new Application();
        applications.add(n);
        return n;
    }

    public void writeToLabels(Labels labels) {
        for(int i = 0; i < applications.size(); ++i) {
            applications.get(i).writeToLabels("applications." + Integer.toString(i) + ".", labels);
        }
    }
}
