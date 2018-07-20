package com.lightbend.rp;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import com.github.zafarkhaja.semver.Version;

public class ReactiveLibChecker {
    MavenProject project;
    Settings settings;
    Log log;

    final String reactiveLibVersion = "0.8.1";

    public ReactiveLibChecker(MavenProject project, Settings settings, Log log) {
        this.project = project;
        this.settings = settings;
        this.log = log;
    }

    // Non-enforcing check
    boolean find(String lib, String version) {
        Version foundVersion = Version.valueOf("0.0.0");
        for (Dependency dep : project.getDependencies()) {
            if (dep.getArtifactId().startsWith(lib)) {
                foundVersion = Version.valueOf(dep.getVersion());
                if(version != null && version.length() > 0) {
                    Version required = Version.valueOf(version);
                    return foundVersion.compareTo(required) >= 0;
                }
            }
        }
        return false;
    }

    // Enforcing check
    public boolean require(String lib, String version) {
        boolean found = false;
        Version foundVersion = Version.valueOf("0.0.0");
        for (Dependency dep : project.getDependencies()) {
            if (dep.getArtifactId().startsWith(lib)) {
                found = true;
                foundVersion = Version.valueOf(dep.getVersion());
                break;
            }
        }

        if (!found) {
            log.error("Lightbend Orchestration required dependency not found: " + lib);
        } else if (version != null && version.length() > 0) {
            Version required = Version.valueOf(version);
            if (foundVersion.compareTo(required) < 0) {
                log.error("Lightbend Orchestration dependency " + lib + " version too old: "
                        + "found " + foundVersion.toString() + ", required " + version);
            }
        }

        return found;
    }

    public void preApplyCheck() throws MojoExecutionException {
        boolean success = true;

        switch(settings.appType) {
            case Basic:
                break;
            case Akka:
                break;
            case Play:
                break;
            case Lagom:
                success = success && require("api-tools", reactiveLibVersion);
                break;
        }

        if (!success) {
            throw new MojoExecutionException("Library dependency requirements not met.");
        }
    }

    public void check() throws MojoExecutionException {
        boolean success = true;
        if (settings.enableCommon) {
            // Common lib is dependency of other, no need to check explicitly
            //success = success && require("reactive-lib-common", reactiveLibVersion);
        }

        if (settings.enableAkkaClusterBootstrap) {
            success = success && require("reactive-lib-akka-cluster-bootstrap", reactiveLibVersion);
        }

        if (settings.enablePlayHttpBinding) {
            success = success && require("reactive-lib-play-http-binding", reactiveLibVersion);
        }

        if (settings.enableServiceDiscovery) {
            // Service discovery is implemented in different libs for java and scala
            // TODO(mitkus): automatically figure out which one to look for
            String javaLibName = "reactive-lib-service-discovery-lagom14-java";
            String scalaLibName = "reactive-lib-service-discovery-lagom14-scala";
            boolean foundJava = find(javaLibName, reactiveLibVersion);
            boolean foundScala = find(scalaLibName, reactiveLibVersion);
            if(!foundJava && !foundScala) {
                log.error("Lightbend Orchestration required dependency not found: " + javaLibName + " or " + scalaLibName);
                success = false;
            }
        }

        if (!success) {
            throw new MojoExecutionException("Library dependency requirements not met.");
        }
    }
}
