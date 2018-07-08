package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.gridfs.Crud
import org.bson.types.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.gridfs.{GridFSBucket, GridFSFile}
import org.mongodb.scala.model.CountOptions
import org.mongodb.scala.{Completed, Document, MongoDatabase, Observable, ReadConcern, ReadPreference, SingleObservable, WriteConcern}

abstract class GridFSDAO(database: MongoDatabase, bucketName: String) extends Crud {

  var bucket = GridFSBucket(database, bucketName)

  protected def gridfsBucket: GridFSBucket = bucket

  def filesCollectionName: String = "%s.%s".format(bucketName, "files")

  def count(filter: Bson = Document(), options: CountOptions = CountOptions()): Observable[Long] = Files.count(filter, options)

  def createrMetadataIndex(key: String, sortAscending: Boolean = true): SingleObservable[String] = Files.createIndexForField(createMetadataKey(key), sortAscending)

  def renameFile(id: ObjectId, newFilename: String): Observable[Completed] = gridfsBucket.rename(id, newFilename)

  def renameFile(file: GridFSFile, newFilename: String): Observable[Completed] = gridfsBucket.rename(file.getId, newFilename)

  def withReadConcern(readConcern: ReadConcern): Unit = {
    bucket = GridFSBucket(database, bucketName).withReadConcern(readConcern)
  }

  def withWriteConcern(writeConcern: WriteConcern): Unit = {
    bucket = GridFSBucket(database, bucketName).withWriteConcern(writeConcern)
  }

  def withChunkSizeBytes(chunkSizeBytes: Int): Unit = {
    bucket = GridFSBucket(database, bucketName).withChunkSizeBytes(chunkSizeBytes)
  }

  def withReadPreference(readPreference: ReadPreference): Unit = {
    bucket = GridFSBucket(database, bucketName).withReadPreference(readPreference)
  }

  def withDisableMD5(disableMD5: Boolean): Unit = {
    bucket = GridFSBucket(database, bucketName).withDisableMD5(disableMD5)
  }

  object Files extends MongoDAO[Document](database, filesCollectionName)

}
