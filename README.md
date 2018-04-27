# reactive-app-maven-plugin

This project is a component of [Lightbend Orchestration](https://developer.lightbend.com/docs/lightbend-orchestration-kubernetes/latest/). Refer to its documentation for usage, examples, and reference information.

This plugin builds Docker images that can be used with CLI tool, [reactive-cli](https://github.com/lightbend/reactive-cli), to automatically create resources for deployment on Kubernetes and DC/OS.

## Installation

For now, plugin is development and not yet available in public Maven repositories. To use it in your projects, clone this repo and install it locally:

```
git clone git@github.com:lightbend/reactive-app-maven-plugin.git
cd reactive-app-maven-plugin
mvn install
```

## Project setup

Add build plugin dependency in your pom.xml:

```
<plugin>
    <groupId>com.lightbend.rp</groupId>
    <artifactId>reactive-app-maven-plugin</artifactId>
    <version>0.2.0-SNAPSHOT</version>
    <configuration>
        <mainClass>com.lightbend.rp.test.akkacluster.SimpleClusterApp</mainClass>
    </configuration>
    <executions>
        <execution>
            <id>build-docker</id>
            <goals>
                <goal>build</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Configuration

If your project isn't built using Lagom or Play, for orchestration to know what to run, you need to specify main class of your project with <mainClass> element in the configuration.

Additionally, you can specify http ingress paths and ports like this:

```
<httpIngressPorts>
    <port>9000</port>
</httpIngressPorts>
<httpIngressPaths>
    <path>/</path>
</httpIngressPaths>
```

### reactive-lib dependencies

This step eventually will be automatic, but for now, to use service discovery and cluster bootstrap features of Lightbend Orchestration you need to manually add reactive-lib libraries to your project dependencies:

```
<dependency>
    <groupId>com.lightbend.rp</groupId>
    <artifactId>reactive-lib-service-discovery-lagom14-java_2.12</artifactId>
    <version>0.7.0</version>
</dependency>
<dependency>
    <groupId>com.lightbend.rp</groupId>
    <artifactId>reactive-lib-akka-cluster-bootstrap_2.12</artifactId>
    <version>0.7.0</version>
</dependency>
```

## Building a docker image

Once project setup is done, you can build a docker image & install it to your docker environment:

```
mvn install
```

You can use `rp` tool with this docker image to generate deployment resources:

```
rp generate-kubernetes-resources "akka-quickstart:1.0" \
  --generate-pod-controllers \
  --generate-services \
  --pod-controller-replicas 2
```

To read more about `rp` go here: [https://developer.lightbend.com/docs/lightbend-orchestration-kubernetes/latest/kubernetes-deployment.html](https://developer.lightbend.com/docs/lightbend-orchestration-kubernetes/latest/kubernetes-deployment.html)