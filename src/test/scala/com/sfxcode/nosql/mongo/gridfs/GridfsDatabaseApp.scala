package com.sfxcode.nosql.mongo.gridfs

import com.sfxcode.nosql.mongo._
import org.bson.types.ObjectId

object GridfsDatabaseApp extends App with GridfsDatabaseFunctions {

  val SourcePath = "src/test/resources/images/"
  val TargetPath = "/tmp/"

  case class ImageMetadata(name:String, group:String="logos", version:Int =1, indexSet:Set[Int] = Set(1,2,3))

  dropImages

  println(imagesCount)
  createIndexOnImages("name")

  val id: ObjectId = insertImage(SourcePath + "scala-logo.png", ImageMetadata("logo1"))

  println(id)

  val file = findImage(id.toString)
  println(file)
  val file2 = findImage(file)
  println(file2)

  downloadImage(file2,TargetPath + file.getFilename)

  insertImage(SourcePath + "scala-logo.jpg", ImageMetadata("logo1", indexSet = Set(5,6,7)))
  println(imagesCount)

  val logos = findImages("group", "logos")

  println(logos)


}
