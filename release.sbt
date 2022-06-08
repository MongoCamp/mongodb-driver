import sbt.url

organization := "dev.mongocamp"
organizationHomepage := Some(url("https://github.com/MongoCamp"))

publishMavenStyle := true

homepage := Some(url("https://mongocamp.github.io/mongodb-driver/"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/MongoCamp/mongodb-driver"),
    "scm:https://github.com/MongoCamp/mongodb-driver.git"
  )
)

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

developers := List(
  Developer(
    id = "mongocamp",
    name = "Tom",
    email = "tom@mongocamp.dev",
    url = url("https://github.com/MongoCamp")
  )
)

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

packageOptions += {
  Package.ManifestAttributes(
    "Created-By"               -> "Simple Build Tool",
    "Built-By"                 -> "mongocamp",
    "Build-Jdk"                -> System.getProperty("java.version"),
    "Specification-Title"      -> name.value,
    "Specification-Version"    -> version.value,
    "Specification-Vendor"     -> organization.value,
    "Implementation-Title"     -> name.value,
    "Implementation-Version"   -> version.value,
    "Implementation-Vendor-Id" -> organization.value,
    "Implementation-Vendor"    -> organization.value
  )
}

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
