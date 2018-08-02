import sbt.url

import scala.sys.process._

name := "simple-mongo"

organization := "com.sfxcode.nosql"

crossScalaVersions := Seq("2.12.6", "2.11.12")

scalaVersion := "2.12.6"

scalacOptions += "-deprecation"

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := BuildInfoKey.ofN(name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.sfxcode.nosql.mongo"
  )


buildInfoOptions += BuildInfoOption.BuildTime


// Test

libraryDependencies += "org.specs2" %% "specs2-core" % "4.3.2" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % Test

libraryDependencies += "com.typesafe" % "config" % "1.3.3" % Test

libraryDependencies += "joda-time" % "joda-time" % "2.10" % Test

libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0" % Test

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.6.0" % Test


libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

buildInfoPackage := "com.sfxcode.nosql.mongo"

buildInfoOptions += BuildInfoOption.BuildTime

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))


version in Paradox := {
  if (isSnapshot.value)
    "git tag -l".!!.split("\r?\n").last
  else version.value
}

paradoxProperties += ("app-version" -> {if (isSnapshot.value)
  "git tag -l".!!.split("\r?\n").last
else version.value})

enablePlugins(ParadoxSitePlugin, ParadoxMaterialThemePlugin)
sourceDirectory in Paradox := sourceDirectory.value / "main" / "paradox"
ParadoxMaterialThemePlugin.paradoxMaterialThemeSettings(Paradox)

paradoxMaterialTheme in Paradox ~= {
  _.withRepository(uri("https://github.com/sfxcode/simple-mongo"))
}

enablePlugins(GhpagesPlugin)

git.remoteRepo := "git@github.com:sfxcode/simple-mongo.git"
ghpagesNoJekyll := true

enablePlugins(SiteScaladocPlugin)
siteSubdirName in SiteScaladoc := "api/latest"

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
    id    = "sfxcode",
    name  = "Tom Lamers",
    email = "tom@sfxcode.com",
    url   = url("https://github.com/sfxcode")
  )
)



