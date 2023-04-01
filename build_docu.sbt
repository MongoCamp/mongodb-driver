import scala.sys.process._

commands += Command.command("ci-deploy-docs")((state: State) => {
  val lowerCaseVersion = version.value.toLowerCase
  if (
    (lowerCaseVersion.contains("snapshot") ||
    lowerCaseVersion.contains("beta") ||
    lowerCaseVersion.contains("rc") ||
    lowerCaseVersion.contains("m"))
  ) {
    state
  }
  else {
    "sh ./deploy_ghpages.sh".!
    state
  }
})
