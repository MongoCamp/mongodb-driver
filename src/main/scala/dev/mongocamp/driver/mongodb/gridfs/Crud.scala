package dev.mongocamp.driver.mongodb.gridfs

import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.{ Document, Observable }

import java.io.InputStream

abstract class Crud extends Search {

  def deleteOne(id: ObjectId): Observable[Unit] = gridfsBucket.delete(id)

  def insertOne(fileName: String, stream: InputStream, metadata: AnyRef = Document(), chunkSizeBytes: Int = 1204 * 256): Observable[ObjectId] = {
    upload(fileName, GridFSStreamObservable(stream, chunkSizeBytes), metadata, chunkSizeBytes)
  }

}
