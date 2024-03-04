package dev.mongocamp.driver.mongodb

import dev.mongocamp.driver.mongodb.database.{ChangeObserver, CollectionStatus, DatabaseProvider}
import dev.mongocamp.driver.mongodb.gridfs.Metadata
import org.bson.types.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.gridfs.{GridFSBucket, GridFSFile}
import org.mongodb.scala.model.CountOptions
import org.mongodb.scala.{Document, Observable, ReadConcern, ReadPreference, SingleObservable, WriteConcern}

abstract class GridFSDAO(provider: DatabaseProvider, bucketName: String) extends Metadata(provider, bucketName) {

  protected var bucket: GridFSBucket = provider.bucket(bucketName)

  protected val databaseName: String = provider.guessDatabaseName(bucketName)

  def addChangeObserver(observer: ChangeObserver[Document]): ChangeObserver[Document] = {
    Files.addChangeObserver(observer: ChangeObserver[Document])
  }

  def fileCollectionStatus: Observable[CollectionStatus] = Files.collectionStatus

  def chunkCollectionStats: Observable[CollectionStatus] = Chunks.collectionStatus

  protected def gridfsBucket: GridFSBucket = bucket

  def count(filter: Bson = Document(), options: CountOptions = CountOptions()): Observable[Long] = Files.count(filter, options)

  def createMetadataIndex(key: String, sortAscending: Boolean = true): SingleObservable[String] = {
    Files.createIndexForField(createMetadataKey(key), sortAscending)
  }

  def dropIndexForName(name: String): SingleObservable[Unit] = Files.dropIndexForName(name)

  def renameFile(id: ObjectId, newFilename: String): Observable[Unit] = gridfsBucket.rename(id, newFilename)

  def renameFile(file: GridFSFile, newFilename: String): Observable[Unit] = gridfsBucket.rename(file.getId, newFilename)

  def withReadConcern(readConcern: ReadConcern): Unit = {
    bucket = GridFSBucket(provider.database(), bucketName).withReadConcern(readConcern)
  }

  def withWriteConcern(writeConcern: WriteConcern): Unit = {
    bucket = GridFSBucket(provider.database(), bucketName).withWriteConcern(writeConcern)
  }

  def withChunkSizeBytes(chunkSizeBytes: Int): Unit = {
    bucket = GridFSBucket(provider.database(), bucketName).withChunkSizeBytes(chunkSizeBytes)
  }

  def withReadPreference(readPreference: ReadPreference): Unit = {
    bucket = GridFSBucket(provider.database(), bucketName).withReadPreference(readPreference)
  }

  override def toString: String = "%s:%s@%s, %s".format(databaseName, bucketName, provider.config, super.toString)

}
