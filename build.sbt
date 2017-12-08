import scala.sys.process._

name := "maven-reactive-app"
organization := "com.lightbend.rp"
organizationName := "Lightbend, Inc."
startYear := Some(2017)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

libraryDependencies ++= Vector(
  "org.apache.maven" % "maven-plugin-api" % "2.0",
  "org.apache.maven.plugin-tools" % "maven-plugin-annotations" % "3.2",
  "org.twdata.maven" % "mojo-executor" % "2.2.0"
)

publishMavenStyle := true
autoScalaLibrary := false
crossPaths := false

// This registers our plugin in with local maven repository
lazy val publishLocalMaven = taskKey[Unit]("Publish local maven plugin.")
publishLocalMaven := {
  publishLocal.value
  publishLocalConfiguration.value.artifacts.foreach( { case (artifact, file) =>
    if (file.toString.endsWith(version.value + ".jar")) {
      Process(Seq(
        "mvn", "install:install-file",
        "-Dfile="+file.toString,
        "-DgroupId="+organization.value,
        "-DartifactId="+name.value,
        "-Dversion="+version.value,
        "-Dpackaging=jar",
        "-DgeneratePom=true",
        "-DcreateChecksum=true"))!
    }
  })
}

homepage := Some(url("https://www.lightbend.com/"))
developers := List(
  Developer("lightbend", "Lightbend Contributors", "", url("https://github.com/lightbend/sbt-reactive-app"))
)

