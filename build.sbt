import sbt.Keys.libraryDependencies

name := "RoundRobin"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "com.typesafe" % "config" % "1.3.1",
  "joda-time" % "joda-time" % "2.9.7",
  "com.github.cb372" %% "scalacache-guava" % "0.10.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.8",
  "org.slf4j" % "slf4j-log4j12" % "1.7.25" % Test,
  "net.codingwell" %% "scala-guice" % "4.1.1")