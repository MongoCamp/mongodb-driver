package dev.mongocamp.driver.mongodb.gridfs

import com.typesafe.scalalogging.LazyLogging
import java.io.OutputStream
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import org.mongodb.scala.Observer

case class GridFSStreamObserver(outputStream: OutputStream) extends Observer[ByteBuffer] with LazyLogging {
  val completed    = new AtomicBoolean(false)
  val resultLength = new AtomicLong(0)

  override def onNext(buffer: ByteBuffer): Unit = {
    val bytes = new Array[Byte](buffer.remaining())
    resultLength.set(resultLength.get() + bytes.length)
    buffer.get(bytes, 0, bytes.length)
    buffer.asInstanceOf[Buffer].clear()
    outputStream.write(bytes)
  }

  override def onError(e: Throwable): Unit = {
    logger.error(e.getMessage, e)
    outputStream.close()
    resultLength.set(-1)
    completed.set(true)
  }

  override def onComplete(): Unit = {
    outputStream.close()
    completed.set(true)
  }
}
