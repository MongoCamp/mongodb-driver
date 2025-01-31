package dev.mongocamp.driver.mongodb.gridfs

import better.files.File
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.model.ImageMetadata
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.bson.types.ObjectId

class GridFSDatabaseSuite extends munit.FunSuite with GridfsDatabaseFunctions {

  test("find file") {
    val fileName = "scala-logo.jpg"

    val file = findImage("filename", fileName)
    assertEquals(file.getFilename, fileName)
  }

  test("insert file and check") {
    val fileName = "scala-logo.png"

    val filePath      = ImageDAOSourcePath + fileName
    val uploadBytes   = File(filePath).bytes.toList
    val oid: ObjectId = insertImage(filePath, ImageMetadata("template1", group = "templates"))

    val file = findImage(oid)
    assertEquals(file.getFilename, fileName)
    assertEquals(file.getMetadata.get("name").toString, "template1")

    ImageFilesDAO.renameFile(oid, "test.png").result()
    assertEquals(findImage(oid).getFilename, "test.png")

    val downloadedFile = File.newTemporaryFile(suffix = fileName)
    val result: Long   = downloadImage(oid, downloadedFile.toString())

    assert(result != -1)

    assert(downloadedFile.exists)

    val downloadBytes = downloadedFile.bytes.toList

    assertEquals(downloadBytes.size, uploadBytes.size)
    assertEquals(downloadBytes, uploadBytes)
  }

  test("update metadata") {
    val files = findImages("group", "logos")
    assertEquals(files.size, 1)
    assertEquals(files.head.getMetadata.get("name").toString, "logo2")

    // update complete metadata for one file
    updateMetadata(files.head, ImageMetadata("logo22"))
    // update metadata entry for all files
    updateMetadataElements(Map(), Map("group" -> "logos3", "newKey" -> "newEntryValue"))

    val file = findImage(files.head)
    assertEquals(file.getMetadata.get("name").toString, "logo22")
    assertEquals(file.getMetadata.get("newKey").toString, "newEntryValue")
  }

  test("find stats in file") {
    val fileStats  = ImageFilesDAO.fileCollectionStatus.result()
    val chunkStats = ImageFilesDAO.chunkCollectionStats.result()

    assert(fileStats.count > 0)
    assert(chunkStats.storageSize > 0)
  }

  override def beforeAll(): Unit = {
    dropImages()
    insertImage(ImageDAOSourcePath + "scala-logo.jpg", ImageMetadata("logo2", indexSet = Set(5, 6, 7)))
    assertEquals(imagesCount, 1L)

    val file = File(ImageDAOTargetPath)
    if (!file.exists) {
      file.createDirectory()
    }
  }

}
