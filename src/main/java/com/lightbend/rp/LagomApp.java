package com.lightbend.rp;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

public class LagomApp implements ReactiveApp {
    BasicApp basic = new BasicApp();

    @Override
    public void apply(MavenProject project, Settings settings, Labels labels) {
        basic.apply(project, settings, labels);

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

            // TODO: use java reflection to call sdObj.services(ld) here
            //System.out.println("Services: " + s);
        } catch(Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
