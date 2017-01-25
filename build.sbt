
name := "simple-mongo"

organization := "com.sfxcode.nosql"

version := "0.7.2"

crossScalaVersions := Seq( "2.12.1","2.11.8")

scalaVersion := "2.11.8"

scalacOptions += "-deprecation"

parallelExecution in Test := false

javacOptions ++= Seq("-source", "1.8")

javacOptions ++= Seq("-target", "1.8")

scalacOptions += "-target:jvm-1.8"

parallelExecution in Test := false


// Test


libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.7" % "test"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.9" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.3.1" % "test"


libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.0"

libraryDependencies += "joda-time" % "joda-time" % "2.9.7"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"


licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayReleaseOnPublish in ThisBuild := false
