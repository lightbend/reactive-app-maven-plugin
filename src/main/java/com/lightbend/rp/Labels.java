package com.lightbend.rp;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.HashMap;

// Helper to collect docker image labels and later write them to the configuration
public class Labels {
    private HashMap<String, String> labelMap;

    public Labels() {
        labelMap = new HashMap<String, String>();
    }

    public void add(String name, String value) {
        labelMap.put(name, value);
    }

    public void writeToConf(Xpp3Dom conf) {
        Xpp3Dom build = conf.getChild("images").getChild("image").getChild("build");
        Xpp3Dom labels = new Xpp3Dom("labels");
        build.addChild(labels);

        for(HashMap.Entry<String, String> e: labelMap.entrySet()) {
            Xpp3Dom label = new Xpp3Dom("com.lightbend.rp." + e.getKey());
            label.setValue("\"" + e.getValue() + "\"");
            labels.addChild(label);
        }
    }
}
