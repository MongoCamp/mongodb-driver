package com.sfxcode.nosql.mongo.gridfs

import java.io.OutputStream
import java.nio.ByteBuffer

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.Observer

case class GridFSStreamObserver(outputStream: OutputStream) extends Observer[ByteBuffer] with LazyLogging {

  override def onNext(buffer: ByteBuffer): Unit = {
    val bytes = new Array[Byte](buffer.remaining())
    buffer.get(bytes, 0, bytes.length)
    buffer.clear()
    outputStream.write(bytes)
  }

  override def onError(e: Throwable): Unit = {
    logger.error(e.getMessage, e)
    outputStream.close()
  }

  override def onComplete(): Unit =
    outputStream.close()
}
