package com.sfxcode.nosql.mongo.gridfs

import better.files.File
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.test.TestDatabase._
import com.sfxcode.nosql.mongo.model.ImageMetadata
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

      val oid: ObjectId = insertImage(ImageDAOSourcePath + fileName, ImageMetadata("template1", group = "templates"))

      val file = findImage(oid)
      file.getFilename must be equalTo fileName
      file.getMetadata.get("name").toString must be equalTo "template1"

      ImageFilesDAO.renameFile(oid, "test.png").result()
      findImage(oid).getFilename must be equalTo "test.png"

      val downloadPath = "/tmp/download_" + fileName
      val result: Long = downloadImage(oid, downloadPath)

      result must not be equalTo(-1)

      File(downloadPath).exists must beTrue

    }

    "update metadata in" in {

      val files = findImages("group", "logos")
      files must haveSize(1)
      files.head.getMetadata.get("name").toString must be equalTo "logo2"

      // update complete metadata for one file
      updateMetadata(files.head, ImageMetadata("logo22", group = "logos"))
      // update metadata entry for all files
      updateMetadataElements(Map(), Map("group" -> "logos3", "newKey" -> "newEntryValue"))

      val file = findImage(files.head)
      file.getMetadata.get("name").toString must be equalTo "logo22"
      file.getMetadata.get("newKey").toString must be equalTo "newEntryValue"

    }

    "find stats in file in" in {
      val fileStats  = ImageFilesDAO.fileCollectionStatus.result()
      val chunkStats = ImageFilesDAO.chunkCollectionStats.result()

      fileStats.count must be greaterThan 0
      chunkStats.storageSize must be greaterThan 0

    }

  }

  override def beforeAll(): Unit = {
    dropImages
    insertImage(ImageDAOSourcePath + "scala-logo.jpg", ImageMetadata("logo2", indexSet = Set(5, 6, 7)))
    imagesCount must be equalTo 1

    val file = File(ImageDAOTargetPath)
    if (!file.exists)
      file.createDirectory()
  }
}
