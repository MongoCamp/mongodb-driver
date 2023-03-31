addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.7")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")

// updates

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")

addSbtPlugin("com.github.fedragon" % "sbt-todolist" % "0.7")

// Release
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.18")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.1.1")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")

addSbtPlugin("dev.quadstingray" %% "sbt-json" % "0.6.3")

addDependencyTreePlugin

// todo remove as soon as possible
dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"

