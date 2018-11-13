package com.sfxcode.nosql.mongo.gridfs

import java.io.InputStream

import com.mongodb.client.gridfs.model.GridFSUploadOptions
import com.sfxcode.nosql.mongo.Converter
import org.bson.types.ObjectId
import org.mongodb.scala.gridfs.AsyncInputStream
import org.mongodb.scala.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream
import org.mongodb.scala.{ Completed, Document, Observable }

abstract class Crud extends Search {

  def deleteOne(id: ObjectId): Observable[Completed] = gridfsBucket.delete(id)

  def insertOne(
    fileName: String,
    stream: InputStream,
    metadata: AnyRef = Document(),
    chunkSizeBytes: Int = chunkSizeBytes): Observable[ObjectId] = {
    val streamToUploadFrom: AsyncInputStream = toAsyncInputStream(stream)
    val metadataDocument = {
      metadata match {
        case document: Document => document
        case _ => Converter.toDocument(metadata)
      }
    }
    val options: GridFSUploadOptions = new GridFSUploadOptions()
      .chunkSizeBytes(1024 * 1204)
      .metadata(metadataDocument)
    gridfsBucket.uploadFromStream(fileName, streamToUploadFrom, options)
  }

}
