package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.gridfs.Metadata
import org.bson.types.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.gridfs.{ GridFSBucket, GridFSFile }
import org.mongodb.scala.model.CountOptions
import org.mongodb.scala.{ Document, Observable, ReadConcern, ReadPreference, SingleObservable, WriteConcern }

abstract class GridFSDAO(provider: DatabaseProvider, bucketName: String) extends Metadata(provider, bucketName) {

  var bucket: GridFSBucket = {
    if (bucketName.contains(DatabaseProvider.CollectionSeparator)) {
      val newDatabaseName = bucketName.substring(0, bucketName.indexOf(DatabaseProvider.CollectionSeparator))
      val newBucketName   = bucketName.substring(bucketName.indexOf(DatabaseProvider.CollectionSeparator) + 1)
      GridFSBucket(provider.database(newDatabaseName), newBucketName)
    } else {
      GridFSBucket(provider.database(), bucketName)
    }
  }

  protected def gridfsBucket: GridFSBucket = bucket

  def count(filter: Bson = Document(), options: CountOptions = CountOptions()): Observable[Long] =
    Files.count(filter, options)

  def createMetadataIndex(key: String, sortAscending: Boolean = true): SingleObservable[String] =
    Files.createIndexForField(createMetadataKey(key), sortAscending)

  def dropIndexForName(name: String): SingleObservable[Void] =
    Files.dropIndexForName(name)

  def renameFile(id: ObjectId, newFilename: String): Observable[Void] =
    gridfsBucket.rename(id, newFilename)

  def renameFile(file: GridFSFile, newFilename: String): Observable[Void] =
    gridfsBucket.rename(file.getId, newFilename)

  def withReadConcern(readConcern: ReadConcern): Unit =
    bucket = GridFSBucket(provider.database(), bucketName).withReadConcern(readConcern)

  def withWriteConcern(writeConcern: WriteConcern): Unit =
    bucket = GridFSBucket(provider.database(), bucketName).withWriteConcern(writeConcern)

  def withChunkSizeBytes(chunkSizeBytes: Int): Unit =
    bucket = GridFSBucket(provider.database(), bucketName).withChunkSizeBytes(chunkSizeBytes)

  def withReadPreference(readPreference: ReadPreference): Unit =
    bucket = GridFSBucket(provider.database(), bucketName).withReadPreference(readPreference)

}
