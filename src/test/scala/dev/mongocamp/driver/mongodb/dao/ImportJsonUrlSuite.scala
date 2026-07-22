package dev.mongocamp.driver.mongodb.dao

import better.files.File
import dev.mongocamp.driver.mongodb.test.TestDatabase.ImportUrlTestDAO
import dev.mongocamp.driver.MongoImplicits
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class ImportJsonUrlSuite extends munit.FunSuite with MongoImplicits {

  private val jsonLine = """{ "name" : "Import Url Test" }"""

  override def beforeEach(context: BeforeEach): Unit = {
    ImportUrlTestDAO.drop().result()
  }

  test("importJsonFile supports a plain file: URL") {
    val tempFile = File.newTemporaryFile(prefix = "import-json-url-file", suffix = ".json")
    try {
      tempFile.appendLine(jsonLine)
      ImportUrlTestDAO.importJsonFile(tempFile.toJava.toURI.toURL).result()
      val count: Long = ImportUrlTestDAO.count()
      assertEquals(count, 1L)
    }
    finally tempFile.delete(swallowIOExceptions = true)
  }

  test("importJsonFile supports a file: URI") {
    val tempFile = File.newTemporaryFile(prefix = "import-json-url-uri", suffix = ".json")
    try {
      tempFile.appendLine(jsonLine)
      ImportUrlTestDAO.importJsonFile(tempFile.toJava.toURI).result()
      val count: Long = ImportUrlTestDAO.count()
      assertEquals(count, 1L)
    }
    finally tempFile.delete(swallowIOExceptions = true)
  }

  test("importJsonFile supports a jar:file: URL (sbt 2 packaged test resources)") {
    val jarFile = File.newTemporaryFile(prefix = "import-json-url-jar", suffix = ".jar")
    try {
      val jarOut = new JarOutputStream(jarFile.newOutputStream)
      try {
        jarOut.putNextEntry(new JarEntry("entry.json"))
        jarOut.write(jsonLine.getBytes("UTF-8"))
        jarOut.closeEntry()
      }
      finally jarOut.close()

      val jarUrl = new java.net.URL(s"jar:${jarFile.toJava.toURI.toURL}!/entry.json")
      ImportUrlTestDAO.importJsonFile(jarUrl).result()
      val count: Long = ImportUrlTestDAO.count()
      assertEquals(count, 1L)
    }
    finally jarFile.delete(swallowIOExceptions = true)
  }
}
