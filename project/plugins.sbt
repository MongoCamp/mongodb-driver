addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.4.4")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

// updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.7.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.5.0")

addSbtPlugin("dev.quadstingray" %% "sbt-json" % "0.8.1")

libraryDependencies += ("com.vdurmont" % "semver4j" % "3.1.0")
