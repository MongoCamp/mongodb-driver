package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.Converter
import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.Observer
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.{DeleteResult, InsertManyResult, InsertOneResult, UpdateResult}

trait CrudObserver[A] extends Crud[A] {

  def insertValue(value: A, observer: Observer[InsertOneResult] = new SimpleObserver[InsertOneResult]): Unit =
    insertOne(value).subscribe(observer)

  def insertValues(values: Seq[A], observer: Observer[InsertManyResult] = new SimpleObserver[InsertManyResult]): Unit =
    insertMany(values).subscribe(observer)

  def replaceValue(value: A, observer: Observer[UpdateResult] = new SimpleObserver[UpdateResult]): Unit =
    replaceOne(value).subscribe(observer)

  def deleteValue(value: A, observer: Observer[DeleteResult] = new SimpleObserver[DeleteResult]): Unit = {
    val oid    = Converter.toDocument(value).get("_id").get
    val filter = equal("_id", oid)
    deleteOne(filter).subscribe(observer)
  }

  def deleteValues(filter: Bson, observer: Observer[DeleteResult] = new SimpleObserver[DeleteResult]): Unit =
    deleteMany(filter).subscribe(observer)

}

class SimpleObserver[T] extends Observer[T] with LazyLogging {
  override def onError(e: Throwable): Unit = logger.error(e.getMessage, e)

  override def onComplete(): Unit = {}

  override def onNext(result: T): Unit = {}
}
