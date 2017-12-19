package com.lightbend.rp;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class Settings {

    public String appName;
    public AppType appType;
    public Long diskSpace;
    public Long memory;
    public Double cpu;

    // Makes string lowercase and capitalizes first letter for parsing app type
    String capitalizeFirst(String str) {
        String res = str.toLowerCase();
        return res.substring(0,1).toUpperCase() + res.substring(1);
    }

    Settings(Xpp3Dom config) {
        if(config != null) {
            for (Xpp3Dom child : config.getChildren()) {
                switch (child.getName()) {
                    case "appName":
                        appName = child.getValue();
                        break;
                    case "appType":
                        appType = AppType.valueOf(capitalizeFirst(child.getValue()));
                        break;
                    case "diskSpace":
                        diskSpace = Long.decode(child.getValue());
                        break;
                    case "memory":
                        memory = Long.decode(child.getValue());
                        break;
                    case "cpu":
                        cpu = Double.parseDouble(child.getValue());
                        break;
                }
            }
        }
    }
}
