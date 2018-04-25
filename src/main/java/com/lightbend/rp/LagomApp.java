package com.lightbend.rp;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

//import com.lightbend.rp.sbtreactiveapp.magic.Lagom$;

public class LagomApp implements ReactiveApp {
    BasicApp basic;

    public LagomApp(Settings settings, Labels labels, Endpoints endpoints) {
        basic = new BasicApp(settings, labels, endpoints);
    }

    private void parseServices(String serviceJson) {
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
            System.out.println("Services: " + servicesJson);
            parseServices(servicesJson);

        } catch(Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
