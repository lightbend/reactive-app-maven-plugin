package com.lightbend.rp;

import org.apache.maven.project.MavenProject;
import org.json.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LagomApp implements ReactiveApp {
    private BasicApp basic;
    private Pattern pathExtractor = Pattern.compile("^\\\\Q(/.*?)\\\\E.*");

    public LagomApp(Settings settings, Labels labels, Endpoints endpoints, Applications applications) {
        basic = new BasicApp(settings, labels, endpoints, applications);

        // Override some defaults
        settings.enableCommon = true;
        settings.enableAkkaClusterBootstrap = true;
        settings.enableServiceDiscovery = true;
        settings.mainClass = "play.core.server.ProdServerStart";
    }

    private String decodePathPattern(String pattern) {
        Matcher m = pathExtractor.matcher(pattern);
        if(m.matches())
            return m.group(1);
        return null;
    }

    private void parseServices(String serviceJson) {
        Endpoints endpoints = basic.getEndpoints();

        JSONArray obj = new JSONArray(serviceJson);
        for(int i = 0; i < obj.length(); ++i) {
            JSONObject o = obj.getJSONObject(i);

            Endpoints.Endpoint e = endpoints.addEndpoint();
            e.name = o.getString("name");
            e.protocol = "http";

            JSONArray acls = o.getJSONArray("acls");
            for(int j = 0; j < acls.length(); ++j) {
                // TODO(mitkus): this handles GET method, what happens with POST/PUT?
                JSONObject acl = acls.getJSONObject(j);

                Endpoints.Endpoint.Ingress ing = e.addIngress();
                ing.type = "http";

                // TODO(mitkus): sbt-reactive-app always puts ports 80, 443; is that correct?
                ing.ports.add("80");
                ing.ports.add("443");

                String path = decodePathPattern(acl.getString("pathPattern"));
                if(path != null)
                    ing.paths.add(path);
            }
        }
    }

    @Override
    public void apply(MavenProject project) {
        basic.apply(project);

        try {
            List<String> l = project.getCompileClasspathElements();
            Stream<URL> u = l.stream().map(i -> {
                try {
                    return new File(i).toURI().toURL();
                }
                catch(Exception e) {
                    return null;
                }
            });

            ClassLoader ld = new java.net.URLClassLoader(u.toArray(URL[]::new));
            Class sdClass = ld.loadClass("com.lightbend.lagom.internal.api.tools.ServiceDetector$");
            Object sdObj = sdClass.getField("MODULE$").get(null);
            Method services = sdObj.getClass().getMethod("services", ClassLoader.class);

            String servicesJson = (String)services.invoke(sdObj, ld);
            parseServices(servicesJson);
        } catch(Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
