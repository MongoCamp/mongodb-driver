package com.sfxcode.nosql.mongo.database

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.model.changestream.ChangeStreamDocument
import org.mongodb.scala.{ Observer, Subscription }

case class ChangeObserver[A](onChangeCallback: ChangeStreamDocument[A] => Unit)
  extends Observer[ChangeStreamDocument[A]]
  with LazyLogging {

  override def onSubscribe(subscription: Subscription): Unit = subscription.request(Long.MaxValue) // Request data

  override def onNext(changeDocument: ChangeStreamDocument[A]): Unit =
    onChangeCallback(changeDocument)

  override def onError(throwable: Throwable): Unit =
    logger.error(throwable.getMessage, throwable)

  override def onComplete(): Unit = {}

}
