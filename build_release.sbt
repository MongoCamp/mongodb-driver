import com.vdurmont.semver4j.Semver
import dev.quadstingray.sbt.json.JsonFile
import sbtrelease.ReleasePlugin.autoImport.ReleaseKeys.versions
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease.ReleasePlugin.runtimeVersion
import scala.sys.process._

releaseVersionBump := sbtrelease.Version.Bump.NextStable

val gitAddAllTask = ReleaseStep(action = st => {
  "git add .".!
  st
})

val setToMyNextVersion = ReleaseStep(action = st => {
  setMyVersion(st.get(versions).get._2, st)
  st
})

val setToMyReleaseVersion = ReleaseStep(action = st => {
  setMyVersion(st.get(versions).get._1, st)
  st
})

def setMyVersion(version: String, state: State): Unit = {
  state.log.warn(s"Set Version in package.json  to $version")
  val json       = JsonFile(file("package.json"))
  val newVersion = version.replace("-SNAPSHOT", ".snapshot")
  json.updateValue("version", newVersion)
  json.write()
}

releaseNextCommitMessage := s"ci: update version after release"
releaseCommitMessage     := s"ci: prepare release of version ${runtimeVersion.value}"

commands += Command.command("ci-release")(
  (state: State) => {
    val semVersion = new Semver(version.value)
    if (semVersion.isStable) {
      Command.process("release with-defaults", state)
    }
    else {
      state
    }
  }
)

releaseProcess := {
  Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    setToMyReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonaRelease"),
    releaseStepCommand("ci-deploy-docs"),
    setToMyNextVersion,
    releaseStepCommand("scalafmtAll"),
    gitAddAllTask,
    commitNextVersion,
    pushChanges
  )
}

Global / useGpgPinentry := true

ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots".at(centralSnapshots))
  else localStaging.value
}

credentials += Credentials("central.sonatype.com", "central.sonatype.com", System.getenv("SONATYPE_USER"), System.getenv("SONATYPE_PASSWORD"))

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
