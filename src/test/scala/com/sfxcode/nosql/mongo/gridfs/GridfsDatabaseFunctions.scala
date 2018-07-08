package com.sfxcode.nosql.mongo.gridfs

import better.files.File
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.gridfs.GridfsDatabase._
import org.bson.types.ObjectId
import org.mongodb.scala.Completed
import org.mongodb.scala.gridfs.GridFSFile

trait GridfsDatabaseFunctions {

  def createIndexOnImages(key:String): String =
    ImageFilesDAO.createrMetadataIndex(key)

  def deleteImage(id: ObjectId): Completed =
    ImageFilesDAO.deleteOne(id)

  def dropImages: Completed = ImageFilesDAO.drop()

  def imagesCount: Long = ImageFilesDAO.count()

  def insertImage(path: String, metadata: AnyRef): ObjectId = {
    val file = File(path)
    val stream = file.newInputStream
    ImageFilesDAO.insertOne(file.name, stream, metadata)
  }

  def downloadImage(id: ObjectId, path: String): Long =
    ImageFilesDAO.downloadToStream(id, File(path).path)

  def findImage(id: ObjectId): GridFSFile = ImageFilesDAO.findById(id)

  def findImages(key:String, value:String): List[GridFSFile] = ImageFilesDAO.findByMetadataValue(key, value)

}
