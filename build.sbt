
name := "simple-mongo"

organization := "com.sfxcode.nosql"

crossScalaVersions := Seq("2.12.3", "2.11.11")

scalaVersion := "2.12.3"

scalacOptions += "-deprecation"

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.sfxcode.nosql.mongo"
  )

buildInfoOptions += BuildInfoOption.BuildTime



// Test

libraryDependencies += "org.specs2" %% "specs2-core" % "4.0.0" % "test"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.3.2" % "test"

libraryDependencies += "joda-time" % "joda-time" % "2.9.9" % "test"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3" % "test"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0"


libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"


licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayReleaseOnPublish in ThisBuild := false
