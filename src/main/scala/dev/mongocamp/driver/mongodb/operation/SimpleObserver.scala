package dev.mongocamp.driver.mongodb.operation

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.Observer

class SimpleObserver[T] extends Observer[T] with LazyLogging {
  override def onError(e: Throwable): Unit = logger.error(e.getMessage, e)

  override def onComplete(): Unit = {}

  override def onNext(result: T): Unit = {}
}
