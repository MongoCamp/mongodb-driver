package dev.mongocamp.driver.mongodb.utils

import better.files.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class FileUtilsSuite extends munit.FunSuite {

  private val jsonLine = """{ "name" : "Import Url Test" }"""

  test("getFileByUrl supports a plain file: URL") {
    val tempFile = File.newTemporaryFile(prefix = "file-utils-url-file", suffix = ".json")
    try {
      tempFile.appendLine(jsonLine)
      val resolved = FileUtils.getFileByUrl(tempFile.toJava.toURI.toURL)
      assertEquals(resolved.contentAsString.trim, jsonLine)
    }
    finally tempFile.delete(swallowIOExceptions = true)
  }

  test("getFileByUrl supports a file: URI") {
    val tempFile = File.newTemporaryFile(prefix = "file-utils-url-uri", suffix = ".json")
    try {
      tempFile.appendLine(jsonLine)
      val resolved = FileUtils.getFileByUrl(tempFile.toJava.toURI)
      assertEquals(resolved.contentAsString.trim, jsonLine)
    }
    finally tempFile.delete(swallowIOExceptions = true)
  }

  test("getFileByUrl supports a jar:file: URL (sbt 2 packaged test resources)") {
    val jarFile = File.newTemporaryFile(prefix = "file-utils-url-jar", suffix = ".jar")
    try {
      val jarOut = new JarOutputStream(jarFile.newOutputStream)
      try {
        jarOut.putNextEntry(new JarEntry("entry.json"))
        jarOut.write(jsonLine.getBytes("UTF-8"))
        jarOut.closeEntry()
      }
      finally jarOut.close()

      val jarUrl   = new java.net.URL(s"jar:${jarFile.toJava.toURI.toURL}!/entry.json")
      val resolved = FileUtils.getFileByUrl(jarUrl)
      assertEquals(resolved.contentAsString, jsonLine)
    }
    finally jarFile.delete(swallowIOExceptions = true)
  }
}
