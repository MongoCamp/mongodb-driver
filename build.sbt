import sbt.url
name := "mongodb-driver"

organization := "dev.mongocamp"

val MongoCampHomepage = "https://www.mongocamp.dev"

organizationHomepage := Some(url(MongoCampHomepage))

homepage := Some(url("https://mongodb-driver.mongocamp.dev"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/MongoCamp/mongodb-driver"),
    "scm:https://github.com/MongoCamp/mongodb-driver.git"
  )
)

developers := List(
  Developer(
    id = "mongocamp",
    name = "MongoCamp-Team",
    email = "info@mongocamp.dev",
    url = url(MongoCampHomepage)
  ),
  Developer(
    id = "sfxcode",
    name = "Tom",
    email = "tom@mongocamp.dev",
    url = url(MongoCampHomepage)
  ),
  Developer(
    id = "quadstingray",
    name = "QuadStingray",
    email = "simon@mongocamp.dev",
    url = url(MongoCampHomepage)
  )
)

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

crossScalaVersions := Seq("2.13.10", "2.12.15")

scalaVersion := crossScalaVersions.value.head

scalacOptions += "-deprecation"

Test / parallelExecution := false

lazy val mongodb = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "dev.mongocamp"
  )

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(ParadoxMaterialThemePlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(
    name := "mongodb-driver-docs",
    scalaVersion := "2.13.8",
    libraryDependencies += "dev.mongocamp"     % "mongodb-driver_2.13" % "2.4.0",
    libraryDependencies += "org.xerial.snappy" % "snappy-java"         % "1.1.8.4",
    publish / skip := true,
    ghpagesNoJekyll := true,
    git.remoteRepo := "git@github.com:MongoCamp/mongodb-driver.git",
    Compile / paradoxMaterialTheme ~= {
      _.withRepository(uri("https://github.com/MongoCamp/mongodb-driver"))
    },
    (Compile / paradoxMarkdownToHtml / excludeFilter) := (Compile / paradoxMarkdownToHtml / excludeFilter).value ||
    ParadoxPlugin.InDirectoryFilter((Compile / paradox / sourceDirectory).value / "includes")
  )
  .dependsOn(mongodb)

buildInfoOptions += BuildInfoOption.BuildTime

resolvers += "Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/snapshots")

// Test

libraryDependencies += "org.specs2" %% "specs2-core" % "4.19.2" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.5" % Test

libraryDependencies += "joda-time" % "joda-time" % "2.12.2" % Test

val circeVersion = "0.14.4"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion % Test)

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.0"

libraryDependencies += "org.xerial.snappy" % "snappy-java" % "1.1.9.1" % Provided

libraryDependencies += "com.github.luben" % "zstd-jni" % "1.5.4-1" % Provided

val MongoJavaServerVersion = "1.43.0"

libraryDependencies += "de.bwaldvogel" % "mongo-java-server" % MongoJavaServerVersion % Provided

libraryDependencies += "de.bwaldvogel" % "mongo-java-server-h2-backend" % MongoJavaServerVersion % Provided

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.2"

libraryDependencies += "com.typesafe" % "config" % "1.4.2"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.9.0"

buildInfoPackage := "dev.mongocamp.driver.mongodb"

buildInfoOptions += BuildInfoOption.BuildTime

scalafmtOnCompile := false

coverageMinimumStmtTotal := 70

coverageFailOnMinimum := true
