package com.lightbend.rp;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;

public class Settings {

    public String appName;
    public String appVersion;
    public AppType appType;
    public Long diskSpace;
    public Long memory;
    public Double cpu;

    public ArrayList<String> httpIngressPorts = new ArrayList<>();
    public ArrayList<String> httpIngressPaths = new ArrayList<>();

    // Makes string lowercase and capitalizes first letter for parsing app type
    private String capitalizeFirst(String str) {
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
                    case "httpIngressPorts":
                        for(Xpp3Dom port : child.getChildren()) {
                            if(port.getName().equals("port")) {
                                httpIngressPorts.add(port.getValue());
                            }
                            else {
                                throw new RuntimeException("Expected element named \"port\", found \"" + port.getName() + "\"");
                            }
                        }
                        break;
                    case "httpIngressPaths":
                        for(Xpp3Dom path : child.getChildren()) {
                            if(path.getName().equals("path")) {
                                httpIngressPaths.add(path.getValue());
                            }
                            else {
                                throw new RuntimeException("Expected element named \"path\", found \"" + path.getName() + "\"");
                            }
                        }
                        break;
                }
            }
        }
    }
}
