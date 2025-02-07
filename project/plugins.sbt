addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.4")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.3.0")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

// updates

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")

addSbtPlugin("com.github.fedragon" % "sbt-todolist" % "0.7")

// Release
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.12.2")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.1.1")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

addSbtPlugin("dev.quadstingray" %% "sbt-json" % "0.7.1")

addSbtPlugin("ch.epfl.scala" % "sbt-scala3-migrate" % "0.7.1")


addDependencyTreePlugin

// todo remove as soon as possible
dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"

libraryDependencies += ("com.vdurmont" % "semver4j" % "3.1.0")
