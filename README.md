# reactive-app-maven-plugin

This project is a component of [Lightbend Orchestration](https://developer.lightbend.com/docs/lightbend-orchestration-kubernetes/latest/). Refer to its documentation for usage, examples, and reference information.

This plugin builds Docker images that can be used with CLI tool, [reactive-cli](https://github.com/lightbend/reactive-cli), to automatically create resources for deployment on Kubernetes and DC/OS.

## Installation

If you want to check out source code and build a local snapshot build, you can do it like this:

```
git clone git@github.com:lightbend/reactive-app-maven-plugin.git
cd reactive-app-maven-plugin
mvn install
```

Otherwise, plugin should be available from public maven repositories.

## Project setup

Add build plugin dependency in your pom.xml:

```
<plugin>
    <groupId>com.lightbend.rp</groupId>
    <artifactId>reactive-app-maven-plugin</artifactId>
    <version>0.3.0</version>
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

If your project isn't built using Lagom or Play, for orchestration to know what to run, you need to specify main class of your project with `<mainClass>` element in the configuration.

Additionally, you can specify HTTP ingress paths and ports like this:

```
<httpIngressPorts>
    <port>9000</port>
</httpIngressPorts>
<httpIngressPaths>
    <path>/</path>
</httpIngressPaths>
```

We also support limiting cpu, disk and memory usage like this:

```
<cpu>0.5</cpu>
<memory>512Mi</memory>
<disk>1Gi</disk>
```


### reactive-lib dependencies

To run your application in Lightbend Orchestration environment, helper libraries must be added as dependencies. They do service discovery and cluster bootstrapping, two
important steps in starting up your application successfully. You can add these libraries in your pom.xml dependency section:

```
<dependency>
    <groupId>com.lightbend.rp</groupId>
    <artifactId>reactive-lib-service-discovery-lagom14-java_2.12</artifactId>
    <version>0.8.1</version>
</dependency>
<dependency>
    <groupId>com.lightbend.rp</groupId>
    <artifactId>reactive-lib-akka-cluster-bootstrap_2.12</artifactId>
    <version>0.8.1</version>
</dependency>
```

Plugin will do its best to complain if you should depend on something and forgot to add it. Rule of thumb is: if you use Akka Cluster features, you
need `reactive-lib-akka-cluster-bootstrap`; if you use Lagom, you also need `reactive-lib-service-discovery` and
`api-tools`.

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

To read more about `rp` go here: <https://developer.lightbend.com/docs/lightbend-orchestration/current/kubernetes-deployment.html>
