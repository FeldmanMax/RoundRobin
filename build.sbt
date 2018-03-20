import sbt.Keys.libraryDependencies

autoScalaLibrary := true
managedScalaInstance := false

lazy val commonSettings = Seq(
  organization := "max.feldman",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "RoundRobin",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "com.typesafe" % "config" % "1.3.1",
      "joda-time" % "joda-time" % "2.9.7")
  )

unmanagedBase := baseDirectory.value / "lib"
exportJars := true