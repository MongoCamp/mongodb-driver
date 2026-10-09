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
    IO.write(file("docs/versions.json"), s"""{"mongocamp": "v${version.value}"}\n""")
    val exitCode = "sh ./deploy_ghpages.sh".!
    if (exitCode != 0) {
      state.log.error(s"Documentation deployment failed with exit code $exitCode")
    }
    state
  }
})
