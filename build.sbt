import sbt.url

name := jsonHandler.value.stringValue("package.json", "name")

organization := jsonHandler.value.stringValue("package.json", "organization")

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

crossScalaVersions := Seq("3.6.4", "2.13.16")

scalaVersion := crossScalaVersions.value.head

scalacOptions += "-deprecation"

Test / parallelExecution := false

lazy val mongodb = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "dev.mongocamp"
  )

buildInfoOptions += BuildInfoOption.BuildTime

resolvers += "Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/snapshots")

libraryDependencies += "joda-time" % "joda-time" % "2.14.0"

val circeVersion = "0.14.12"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += ("org.mongodb.scala" %% "mongo-scala-driver" % "5.4.0").cross(CrossVersion.for3Use2_13)

val MongoJavaServerVersion = "1.46.0"

libraryDependencies += "de.bwaldvogel" % "mongo-java-server" % MongoJavaServerVersion % Provided

libraryDependencies += "de.bwaldvogel" % "mongo-java-server-h2-backend" % MongoJavaServerVersion % Provided

libraryDependencies += "org.xerial.snappy" % "snappy-java" % "1.1.10.7" % Provided

libraryDependencies += "com.github.luben" % "zstd-jni" % "1.5.7-2" % Provided

libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "10.1.0"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.2"

libraryDependencies += "com.typesafe" % "config" % "1.4.3"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

//libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.12.0"

libraryDependencies += "com.vdurmont" % "semver4j" % "3.1.0"

libraryDependencies += "com.github.jsqlparser" % "jsqlparser" % "5.1"

buildInfoPackage := "dev.mongocamp.driver.mongodb"

buildInfoOptions += BuildInfoOption.BuildTime

scalafmtOnCompile := false

coverageMinimumStmtTotal := 70

coverageFailOnMinimum := true

jsonFiles += (baseDirectory.value / "package.json")
