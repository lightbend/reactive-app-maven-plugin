import scala.sys.process._

name := "reactive-app-maven-plugin"
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

// This runs integration tests
lazy val runIntegrationTests = taskKey[Unit]("Run integration tests.")
runIntegrationTests := {
  publishM2.value

  val log = streams.value.log
  val base = baseDirectory.value

  def tests(base: File) = ((base / "src" / "it") * AllPassFilter).filter(_.isDirectory)

  def exec(cmd: Seq[String], cwd: File): Boolean = {
    Process(cmd, cwd = cwd) ! log == 0
  }

  tests(base).get.foreach(testDir => {
    val testName = testDir.relativeTo(base).get.toString
    log.info(s"Running test in $testName")

    if ( exec(Seq("mvn", "package"), testDir)
      && exec(Seq("mvn", "-e", "reactive-app:docker"), testDir)) {
      log.success(s"Test $testName passed")
    } else {
      log.error(s"Test $testName failed")
      throw new TestsFailedException
    }
  })
}

homepage := Some(url("https://www.lightbend.com/"))
developers := List(
  Developer("lightbend", "Lightbend Contributors", "", url("https://github.com/lightbend/reactive-app-maven-plugin"))
)

