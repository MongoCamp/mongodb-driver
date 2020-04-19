package com.sfxcode.nosql.mongo.gridfs

import better.files.File
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.gridfs.GridfsDatabase._
import org.bson.types.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.gridfs.GridFSFile
import org.mongodb.scala.result.UpdateResult

trait GridfsDatabaseFunctions {

  def createIndexOnImages(key: String): String =
    ImageFilesDAO.createMetadataIndex(key)

  def dropIndexOnImages(key: String): Void =
    ImageFilesDAO.dropIndexForName(key)

  def deleteImage(id: ObjectId): Void =
    ImageFilesDAO.deleteOne(id)

  def dropImages: Void = ImageFilesDAO.drop()

  def imagesCount: Long = ImageFilesDAO.count()

  def insertImage(path: String, metadata: AnyRef): ObjectId = {
    val file = File(path)
    ImageFilesDAO.insertOne(file.name, file.newInputStream, metadata, bufferSize = 1024 * 4)
  }

  def downloadImage(id: ObjectId, path: String): Unit = {
    val file      = File(path)
    val start     = System.currentTimeMillis()
    val size: Int = ImageFilesDAO.downloadToStream(id, file.newOutputStream)

    println(
      "file: %s with size %s Bytes written in %s ms "
        .format(file.pathAsString, size, System.currentTimeMillis() - start)
    )
  }

  def findImage(id: ObjectId): GridFSFile = ImageFilesDAO.findById(id)

  def findImage(key: String, value: String): GridFSFile = ImageFilesDAO.find(key, value)

  def findImages(key: String, value: String): List[GridFSFile] = ImageFilesDAO.findByMetadataValue(key, value)

  def updateMetadata(oid: ObjectId, value: Any): UpdateResult =
    ImageFilesDAO.updateMetadata(oid, value).headResult()

  def updateMetadataElements(filter: Bson, elements: Map[String, Any]): UpdateResult =
    ImageFilesDAO.updateMetadataElements(filter, elements).headResult()
}
