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

  val artifacts = publishLocalConfiguration.value.artifacts.map(_._2.toString)
  val jarFile = artifacts.find(_.endsWith(version.value + ".jar"))
  val pomFile = artifacts.find(_.endsWith(version.value + ".pom"))

  if(jarFile.isDefined && pomFile.isDefined) {
      Process(Seq(
        "mvn", "install:install-file",
        "-Dfile="+jarFile.get,
        "-DpomFile="+pomFile.get,
        "-DgroupId="+organization.value,
        "-DartifactId="+name.value,
        "-Dversion="+version.value,
        "-Dpackaging=jar",
        "-DcreateChecksum=true"))!
  }
}

// This runs integration tests
lazy val runIntegrationTests = taskKey[Unit]("Run integration tests.")
runIntegrationTests := {
  publishLocalMaven.value

  val log = streams.value.log
  val base = baseDirectory.value

  def tests(base: File) = ((base / "src" / "it") * AllPassFilter).filter(_.isDirectory)

  tests(base).get.foreach( testDir => {
    val testName = testDir.relativeTo(base).get.toString
    log.info(s"Running test in $testName")

    (Process(Seq("mvn", "reactive-app:docker"), cwd = testDir) ! log) match {
      case 0 => log.success(s"Test $testName passed")
      case _ => log.error(s"Test $testName failed")
    }
  })
}

homepage := Some(url("https://www.lightbend.com/"))
developers := List(
  Developer("lightbend", "Lightbend Contributors", "", url("https://github.com/lightbend/sbt-reactive-app"))
)

