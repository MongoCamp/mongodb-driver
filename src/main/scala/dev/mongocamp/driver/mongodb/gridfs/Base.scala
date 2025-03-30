package dev.mongocamp.driver.mongodb.gridfs

import better.files.File
import com.mongodb.client.gridfs.model.GridFSUploadOptions
import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.Converter
import java.io.OutputStream
import java.nio.ByteBuffer
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.documentToUntypedDocument
import org.mongodb.scala.gridfs.GridFSBucket
import org.mongodb.scala.gridfs.GridFSDownloadObservable
import org.mongodb.scala.Document
import org.mongodb.scala.Observable
import org.mongodb.scala.ReadConcern
import org.mongodb.scala.ReadPreference
import org.mongodb.scala.WriteConcern

abstract class Base extends LazyLogging {

  protected def gridfsBucket: GridFSBucket

  def createMetadataKey(key: String): String = {
    var metadataKey = key
    if (!metadataKey.startsWith("metadata")) {
      metadataKey = "%s.%s".format("metadata", key)
    }
    metadataKey
  }

  def drop(): Observable[Unit] = gridfsBucket.drop()

  def bucketName: String = gridfsBucket.bucketName

  def chunkSizeBytes: Int = gridfsBucket.chunkSizeBytes

  def writeConcern: WriteConcern = gridfsBucket.writeConcern

  def readPreference: ReadPreference = gridfsBucket.readPreference

  def readConcern: ReadConcern = gridfsBucket.readConcern

  def upload(fileName: String, source: Observable[ByteBuffer], metadata: AnyRef = Document(), chunkSizeBytes: Int = 1024 * 256): Observable[ObjectId] = {
    val metadataDocument = {
      metadata match {
        case document: Document => document
        case _                  => Converter.toDocument(metadata)
      }
    }
    val options: GridFSUploadOptions = new GridFSUploadOptions().chunkSizeBytes(chunkSizeBytes).metadata(metadataDocument)
    gridfsBucket.uploadFromObservable(fileName, source, options)
  }

  def uploadFile(fileName: String, file: File, metadata: AnyRef = Document(), chunkSizeBytes: Int = 1204 * 256): Observable[ObjectId] = {
    upload(fileName, GridFSStreamObservable(file.newInputStream, chunkSizeBytes), metadata, chunkSizeBytes)
  }

  def download(oid: ObjectId): GridFSDownloadObservable = gridfsBucket.downloadToObservable(oid)

  def download(id: ObjectId, file: File): GridFSStreamObserver = download(id, file.newOutputStream)

  def downloadFileResult(id: ObjectId, file: File): Long = streamObserverResult(download(id, file))

  def download(oid: ObjectId, outputStream: OutputStream): GridFSStreamObserver = {
    val observable: GridFSDownloadObservable = gridfsBucket.downloadToObservable(oid)
    val observer                             = GridFSStreamObserver(outputStream)
    observable.subscribe(observer)
    observer
  }

  def downloadStreamResult(id: ObjectId, outputStream: OutputStream): Long = streamObserverResult(download(id, outputStream))

  protected def streamObserverResult(observer: GridFSStreamObserver): Long = {
    while (!observer.completed.get) {}
    observer.resultLength.get()
  }

}
