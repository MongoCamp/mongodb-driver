import sbt.url
import ReleaseTransformations._

name := "simple-mongo"

organization := "com.sfxcode.nosql"

crossScalaVersions := Seq("2.13.5", "2.12.12")

scalaVersion := crossScalaVersions.value.head

scalacOptions += "-deprecation"

Test / parallelExecution := false

lazy val simple_mongo = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.sfxcode.nosql.mongo"
  )

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(ParadoxMaterialThemePlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(
    name := "simple mongo docs",
    scalaVersion := "2.13.3",
    resolvers += "SFXCode" at "https://dl.bintray.com/sfxcode/maven/",
    libraryDependencies += "com.sfxcode.nosql" % "simple-mongo_2.13" % "2.0.5",
    libraryDependencies += "org.xerial.snappy" % "snappy-java"       % "1.1.7.3",
    publish / skip := true,
    ghpagesNoJekyll := true,
    git.remoteRepo := "git@github.com:sfxcode/simple-mongo.git",
    Compile / paradoxMaterialTheme ~= {
      _.withRepository(uri("https://github.com/sfxcode/simple-mongo"))
    },
    (Compile / paradoxMarkdownToHtml / excludeFilter) := (Compile / paradoxMarkdownToHtml / excludeFilter).value ||
    ParadoxPlugin.InDirectoryFilter((Compile / paradox / sourceDirectory).value / "includes")
  )
  .dependsOn(simple_mongo)

buildInfoOptions += BuildInfoOption.BuildTime

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// Test

libraryDependencies += "org.specs2" %% "specs2-core" % "4.11.0" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % Test

libraryDependencies += "joda-time" % "joda-time" % "2.10.10" % Test

libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.11" % Test

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.2.3"

libraryDependencies += "org.xerial.snappy" % "snappy-java" % "1.1.8.4" % Provided

libraryDependencies += "com.github.luben" % "zstd-jni" % "1.4.9-5" % Provided

val MongoJavaServerVersion = "1.38.0"

libraryDependencies += "de.bwaldvogel" % "mongo-java-server" % MongoJavaServerVersion % Provided

libraryDependencies += "de.bwaldvogel" % "mongo-java-server-h2-backend" % MongoJavaServerVersion % Provided

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1"

libraryDependencies += "com.typesafe" % "config" % "1.4.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"

libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.4"

buildInfoPackage := "com.sfxcode.nosql.mongo"

buildInfoOptions += BuildInfoOption.BuildTime

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// publish

releaseCrossBuild := true

ThisBuild / bintrayReleaseOnPublish := true

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

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions,           // : ReleaseStep
  runClean,                  // : ReleaseStep
  runTest,                   // : ReleaseStep
  setReleaseVersion,         // : ReleaseStep
  commitReleaseVersion,      // : ReleaseStep, performs the initial git checks
  tagRelease,                // : ReleaseStep
  publishArtifacts,          // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,            // : ReleaseStep
  commitNextVersion,         // : ReleaseStep
  pushChanges                // : ReleaseStep, also checks that an upstream branch is properly configured
)

scalafmtOnCompile := false

coverageMinimum := 70

coverageFailOnMinimum := true
