package com.sfxcode.nosql.mongo.gridfs

import java.io.InputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.{ Observable, Observer, Subscription }

case class GridFSStreamObservable(inputStream: InputStream, bufferSize: Int = 1024 * 64)
  extends Observable[ByteBuffer]
  with LazyLogging {
  val isPublishing = new AtomicBoolean(false)
  val buffer = new Array[Byte](bufferSize)

  override def subscribe(subscriber: Observer[_ >: ByteBuffer]): Unit = {
    isPublishing.set(true)
    subscriber.onSubscribe(GridFSSubscription(subscriber))
  }

  case class GridFSSubscription(subscriber: Observer[_ >: ByteBuffer]) extends Subscription {

    override def request(n: Long): Unit =
      try {
        val len = inputStream.read(buffer)
        if (len >= 0 && isPublishing.get()) {
          val byteBuffer = ByteBuffer.wrap(buffer, 0, len)
          subscriber.onNext(byteBuffer)
        } else {
          subscriber.onComplete()
          inputStream.close()
        }
      } catch {
        case e: InterruptedException =>
          Thread.currentThread.interrupt()
          subscriber.onError(e)
          inputStream.close()
          logger.error(e.getMessage, e)
        case t: Throwable =>
          subscriber.onError(t)
          inputStream.close()
          logger.error(t.getMessage, t)
      }

    override def unsubscribe(): Unit =
      isPublishing.set(false)

    override def isUnsubscribed: Boolean = !isPublishing.get()
  }
}
