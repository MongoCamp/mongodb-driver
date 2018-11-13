package com.sfxcode.nosql.mongo.gridfs

import java.nio.channels.AsynchronousFileChannel
import java.nio.file.{ OpenOption, Path, StandardOpenOption }

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.gridfs.GridFSBucket
import org.mongodb.scala.gridfs.helpers.AsynchronousChannelHelper.channelToOutputStream
import org.mongodb.scala.{ Completed, Observable, ReadConcern, ReadPreference, WriteConcern }

abstract class Base extends LazyLogging {

  protected def gridfsBucket: GridFSBucket

  def createMetadataKey(key: String): String = {
    var metadataKey = key
    if (!metadataKey.startsWith("metadata")) {
      metadataKey = "%s.%s".format("metadata", key)
    }
    metadataKey
  }

  def downloadToStream(
    oid: ObjectId,
    outputPath: Path,
    openOptions: Seq[OpenOption] = List(StandardOpenOption.CREATE, StandardOpenOption.WRITE)): Observable[Long] = {
    val streamToDownloadTo: AsynchronousFileChannel =
      AsynchronousFileChannel.open(outputPath, openOptions: _*)
    gridfsBucket.downloadToStream(oid, channelToOutputStream(streamToDownloadTo))
  }

  def drop(): Observable[Completed] = gridfsBucket.drop()

  def bucketName: String = gridfsBucket.bucketName

  def chunkSizeBytes: Int = gridfsBucket.chunkSizeBytes

  def writeConcern: WriteConcern = gridfsBucket.writeConcern

  def readPreference: ReadPreference = gridfsBucket.readPreference

  def readConcern: ReadConcern = gridfsBucket.readConcern

  def disableMD5: Boolean = gridfsBucket.disableMD5

}
