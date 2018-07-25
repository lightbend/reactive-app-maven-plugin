package com.lightbend.rp;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.Channels;
import java.nio.ByteBuffer;
import static java.nio.file.StandardOpenOption.*;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "merge-config", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class MergeConfigMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;

    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    private Plugin getThisPlugin() {
        for(Plugin plugin : mavenProject.getBuildPlugins()) {
            if(plugin.getArtifactId().equals("reactive-app-maven-plugin")) {
                return plugin;
            }
        }
        return null;
    }

    public void execute() throws MojoExecutionException {
        Log log = getLog();
        Xpp3Dom pluginConf = (Xpp3Dom)getThisPlugin().getConfiguration();
        Settings settings = new Settings();
        settings.read(pluginConf);
        if(settings.enableAkkaClusterBootstrap
            || settings.enableStatus) {
            log.info("Executing merge-config");
            try {
                mergeConfigFiles(log);
            } catch (Throwable e) {
                throw new MojoExecutionException("An error occured during merge config", e);
            }
        }
    }

    private void mergeConfigFiles(Log log) throws Throwable {
        List<String> cp = mavenProject.getCompileClasspathElements();
        ArrayList<URL> urls = new ArrayList();
        for (String s: cp) {
            urls.add(new File(s).toURL());
        }
        ClassLoader ld = new java.net.URLClassLoader(urls.toArray(new URL[] {}));
        Enumeration<URL> toolingConfigs = ld.getResources("rp-tooling.conf");
        ByteBuffer newLine = ByteBuffer.wrap(System.getProperty("line.separator").getBytes("UTF-8"));
        File outFile = new File(outputDirectory, "application.conf");
        try (FileChannel out = FileChannel.open(outFile.toPath(), CREATE, APPEND)) {
            for (; toolingConfigs.hasMoreElements();) {
                URL config = toolingConfigs.nextElement();
                log.info("Merging config file " + config.toString());
                out.write(newLine);
                out.write(newLine);
                try (ReadableByteChannel in = Channels.newChannel(config.openStream())) {
                    out.transferFrom(in, out.position(), Integer.MAX_VALUE);
                }
            }
        }
    }
}
