package com.lightbend.rp;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

public class AppTypeDetector {
    public static AppType detect(MavenProject project) {
        boolean hasAkka = false;
        boolean hasPlay = false;
        boolean hasLagom = false;

        for(Dependency dep : project.getDependencies()) {
            String artifactId = dep.getArtifactId();
            if(artifactId.startsWith("akka-actor") || artifactId.startsWith("akka-cluster"))
                hasAkka = true;
            if(artifactId.startsWith("play"))
                hasPlay = true;
            if(artifactId.startsWith("lagom-javadsl-server") || artifactId.startsWith("lagom-scaladsl-server"))
                hasLagom = true;
        }

        // TODO(mitkus): Handle cases when multiple app types are detected at once.
        if(hasLagom)
            return AppType.Lagom;
        if(hasPlay)
            return AppType.Play;
        if(hasAkka)
            return AppType.Akka;
        return AppType.Basic;
    }
}
