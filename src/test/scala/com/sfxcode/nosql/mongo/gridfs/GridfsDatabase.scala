package com.sfxcode.nosql.mongo.gridfs

import com.sfxcode.nosql.mongo.GridFSDAO
import com.sfxcode.nosql.mongo.database.DatabaseProvider

/**
  * GridFS Database Sample
  */
object GridfsDatabase {
  val SourcePath = "src/test/resources/images/"
  val TargetPath = "/tmp/_files/"

  case class ImageMetadata(name: String, group: String = "logos", version: Int = 1, indexSet: Set[Int] = Set(1, 2, 3))

  val provider = DatabaseProvider("test")

  object ImageFilesDAO extends GridFSDAO(provider, "images")

}
