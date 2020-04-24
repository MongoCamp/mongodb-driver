package com.sfxcode.nosql.mongo.gridfs

import java.io.InputStream

import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.{Document, Observable}

abstract class Crud extends Search {

  def deleteOne(id: ObjectId): Observable[Void] = gridfsBucket.delete(id)

  def insertOne(
      fileName: String,
      stream: InputStream,
      metadata: AnyRef = Document(),
      chunkSizeBytes: Int = 1204 * 256,
      bufferSize: Int = 1024 * 64
  ): Observable[ObjectId] =
    upload(fileName, GridFSStreamObservable(stream, bufferSize), metadata, chunkSizeBytes)

}
