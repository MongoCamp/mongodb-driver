import dev.quadstingray.sbt.json.JsonFile

val json = JsonFile(file("package.json"))

ThisBuild / version := json.stringValue("version").trim.toLowerCase.replace(".snapshot", "-SNAPSHOT").trim