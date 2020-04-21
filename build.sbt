import sbt.url

name := "simple-mongo"

organization := "com.sfxcode.nosql"

crossScalaVersions := Seq("2.13.1", "2.12.11")

scalaVersion := crossScalaVersions.value.head

scalacOptions += "-deprecation"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := BuildInfoKey.ofN(name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.sfxcode.nosql.mongo"
  )

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(ParadoxMaterialThemePlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(
    name := "simple mongo docs",
    publish / skip := true,
    ghpagesNoJekyll := true,
    git.remoteRepo := "git@github.com:sfxcode/simple-mongo.git",
    Compile / paradoxMaterialTheme ~= {
      _.withRepository(uri("https://github.com/sfxcode/simple-mongo"))

    }
  )

buildInfoOptions += BuildInfoOption.BuildTime

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Test

libraryDependencies += "org.specs2" %% "specs2-core" % "4.9.3" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % Test

libraryDependencies += "joda-time" % "joda-time" % "2.10.5" % Test

libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.7" % Test

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0" % Test

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.0.2"

libraryDependencies += "org.xerial.snappy" % "snappy-java" % "1.1.7.3" % Provided

libraryDependencies += "com.github.luben" % "zstd-jni" % "1.4.4-9" % Provided

libraryDependencies += "com.typesafe" % "config" % "1.4.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

buildInfoPackage := "com.sfxcode.nosql.mongo"

buildInfoOptions += BuildInfoOption.BuildTime

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// publish

releaseCrossBuild := true

bintrayReleaseOnPublish in ThisBuild := true

publishMavenStyle := true

homepage := Some(url("https://github.com/sfxcode/simple-mongo"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/sfxcode/simple-mongo"),
    "scm:https://github.com/sfxcode/simple-mongo.git"
  )
)

developers := List(
  Developer(
    id = "sfxcode",
    name = "Tom Lamers",
    email = "tom@sfxcode.com",
    url = url("https://github.com/sfxcode")
  )
)

coverageMinimum := 60

coverageFailOnMinimum := true
