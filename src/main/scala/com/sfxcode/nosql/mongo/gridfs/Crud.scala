package com.sfxcode.nosql.mongo.gridfs

import java.nio.ByteBuffer

import com.mongodb.client.gridfs.model.GridFSUploadOptions
import com.sfxcode.nosql.mongo.Converter
import org.bson.types.ObjectId
import org.mongodb.scala.{ Document, Observable }

abstract class Crud extends Search {

  def deleteOne(id: ObjectId): Observable[Void] = gridfsBucket.delete(id)

  def insertOne(fileName: String,
                source: Observable[ByteBuffer],
                metadata: AnyRef = Document(),
                chunkSizeBytes: Int = 1024 * 1204): Observable[ObjectId] = {
    val metadataDocument = {
      metadata match {
        case document: Document => document
        case _                  => Converter.toDocument(metadata)
      }
    }
    val options: GridFSUploadOptions = new GridFSUploadOptions()
      .chunkSizeBytes(chunkSizeBytes)
      .metadata(metadataDocument)
    gridfsBucket.uploadFromObservable(fileName, source, options)
  }

  def upload(fileName: String,
             source: Observable[ByteBuffer],
             metadata: AnyRef = Document(),
             chunkSizeBytes: Int = 1024 * 1204): Observable[ObjectId] =
    insertOne(fileName, source, metadata, chunkSizeBytes)



}
