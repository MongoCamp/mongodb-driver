package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.bson.BsonConverter._

import com.typesafe.scalalogging.LazyLogging
import org.bson.BsonValue
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.{ Completed, DistinctObservable, Document, MongoCollection, Observable, Observer }

import scala.reflect.ClassTag

abstract class Base[A]()(implicit ct: ClassTag[A]) extends LazyLogging {

  protected def coll: MongoCollection[A]

  def count(filter: Bson = Document()): Observable[Long] = coll.count(filter)

  def countResult(filter: Bson = Document()): Long = count(filter)

  def distinct[S <: Any](fieldName: String, filter: Bson = Document()): DistinctObservable[BsonValue] = {
    coll.distinct[BsonValue](fieldName, filter)
  }

  def distinctResult[S <: Any](fieldName: String, filter: Bson = Document()): Seq[S] = {
    distinct(fieldName, filter).results().map(v => fromBson(v).asInstanceOf[S])
  }

  def drop(): Observable[Completed] = coll.drop()

  def dropResult(): Completed = drop().headResult()

}

class SimpleCompletedObserver[T] extends Observer[T] with LazyLogging {
  override def onError(e: Throwable): Unit = logger.error(e.getMessage, e)

  override def onComplete(): Unit = {}

  override def onNext(result: T): Unit = {}
}

