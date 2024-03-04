package dev.mongocamp.driver.mongodb.gridfs

import better.files.File
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.model.ImageMetadata
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.bson.types.ObjectId
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class GridFSDatabaseSpec extends Specification with GridfsDatabaseFunctions with BeforeAll {

  "GridFSDatabase" should {

    "find file in" in {
      val fileName = "scala-logo.jpg"

      val file = findImage("filename", fileName)
      file.getFilename must be equalTo fileName

    }

    "insert file and in" in {
      val fileName = "scala-logo.png"

      val filePath = ImageDAOSourcePath + fileName
      val uploadBytes = File(filePath).bytes.toList
      val oid: ObjectId = insertImage(filePath, ImageMetadata("template1", group = "templates"))

      val file = findImage(oid)
      file.getFilename must be equalTo fileName
      file.getMetadata.get("name").toString must be equalTo "template1"

      ImageFilesDAO.renameFile(oid, "test.png").result()
      findImage(oid).getFilename must be equalTo "test.png"

      val downloadedFile = File.newTemporaryFile(suffix = fileName)
      val result: Long = downloadImage(oid, downloadedFile.toString())

      result must not be equalTo(-1)

      downloadedFile.exists must beTrue

      val downloadBytes = downloadedFile.bytes.toList

      downloadBytes.size must be equalTo uploadBytes.size

      downloadBytes must be equalTo uploadBytes

    }

    "update metadata in" in {

      val files = findImages("group", "logos")
      files must haveSize(1)
      files.head.getMetadata.get("name").toString must be equalTo "logo2"

      // update complete metadata for one file
      updateMetadata(files.head, ImageMetadata("logo22"))
      // update metadata entry for all files
      updateMetadataElements(Map(), Map("group" -> "logos3", "newKey" -> "newEntryValue"))

      val file = findImage(files.head)
      file.getMetadata.get("name").toString must be equalTo "logo22"
      file.getMetadata.get("newKey").toString must be equalTo "newEntryValue"

    }

    "find stats in file in" in {
      val fileStats = ImageFilesDAO.fileCollectionStatus.result()
      val chunkStats = ImageFilesDAO.chunkCollectionStats.result()

      fileStats.count must be greaterThan 0
      chunkStats.storageSize must be greaterThan 0

    }

  }

  override def beforeAll(): Unit = {
    dropImages()
    insertImage(ImageDAOSourcePath + "scala-logo.jpg", ImageMetadata("logo2", indexSet = Set(5, 6, 7)))
    imagesCount must be equalTo 1

    val file = File(ImageDAOTargetPath)
    if (!file.exists) {
      file.createDirectory()
    }
  }
}
