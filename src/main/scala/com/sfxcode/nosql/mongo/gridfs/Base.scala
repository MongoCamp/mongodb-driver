package com.sfxcode.nosql.mongo.gridfs

import java.nio.channels.AsynchronousFileChannel
import java.nio.file.{OpenOption, Path, StandardOpenOption}

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.gridfs.{GridFSBucket, GridFSDownloadObservable}
import org.mongodb.scala.{Observable, ReadConcern, ReadPreference, WriteConcern}

abstract class Base extends LazyLogging {

  protected def gridfsBucket: GridFSBucket

  def createMetadataKey(key: String): String = {
    var metadataKey = key
    if (!metadataKey.startsWith("metadata")) {
      metadataKey = "%s.%s".format("metadata", key)
    }
    metadataKey
  }

  def download(oid: ObjectId):GridFSDownloadObservable = {
    gridfsBucket.downloadToObservable(oid)
  }

  def drop(): Observable[Void] = gridfsBucket.drop()

  def bucketName: String = gridfsBucket.bucketName

  def chunkSizeBytes: Int = gridfsBucket.chunkSizeBytes

  def writeConcern: WriteConcern = gridfsBucket.writeConcern

  def readPreference: ReadPreference = gridfsBucket.readPreference

  def readConcern: ReadConcern = gridfsBucket.readConcern

}
