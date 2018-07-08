package com.sfxcode.nosql.mongo.gridfs

import better.files.File
import com.sfxcode.nosql.mongo.gridfs.GridfsDatabase._
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

    "find files by metadata valuein" in {
      val fileName = "scala-logo.jpg"

      var files = findImages("group", "unknown")
      files must haveSize(0)

      files = findImages("group", "templates")
      files must haveSize(1)
    }

    "insert file in" in {
      val fileName = "scala-logo.png"

      val oid: ObjectId = insertImage(SourcePath + fileName, ImageMetadata("template1", group = "templates"))

      val file = findImage(oid)
      file.getFilename must be equalTo fileName

    }

  }

  override def beforeAll(): Unit = {
    dropImages
    insertImage(SourcePath + "scala-logo.jpg", ImageMetadata("logo2", indexSet = Set(5, 6, 7)))
    imagesCount must be equalTo 1

    val file = File(TargetPath)
    if (!file.exists)
      file.createDirectory()
  }
}
