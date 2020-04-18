package com.sfxcode.nosql.mongo.gridfs

import java.io.FileOutputStream
import java.nio.ByteBuffer

import com.sfxcode.nosql.mongo._
import org.bson.types.ObjectId
import com.sfxcode.nosql.mongo.gridfs.GridfsDatabase._

object GridfsDatabaseApp extends App with GridfsDatabaseFunctions {

  dropImages

  println(imagesCount)
  createIndexOnImages("name")

  val id: ObjectId = insertImage(SourcePath + "scala-logo.png", ImageMetadata("logo1"))

  println(id)

  val file = findImage(id.toString)
  println(file)

  val imageFile = findImage(file)

  println(imageFile.getChunkSize)
  println(imageFile.getMetadata.get("indexSet"))

  downloadImage(imageFile, TargetPath + file.getFilename)

  insertImage(SourcePath + "scala-logo.jpg", ImageMetadata("logo2", indexSet = Set(5, 6, 7)))
  println(imagesCount)

  val logos = findImages("group", "logos")

  println(logos)

}
