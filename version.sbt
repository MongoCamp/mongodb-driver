import scala.io.Source
import scala.tools.nsc.io.File

ThisBuild / version := {
  val packageJsonFile   = File("package.json")
  val source            = Source.fromFile(packageJsonFile.toURI)
  val versionPattern    = "\"version\":(.*?)\"(.*?)\"".r
  val versionPartString = versionPattern.findFirstIn(source.mkString).get
  val replacedVersion   = versionPartString.replace("\"version\":", "").replace("\"", "").replace("\",", "").trim.trim.trim
  replacedVersion.toLowerCase.replace(".snapshot", "-SNAPSHOT")
}
