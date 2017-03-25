package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.bson.BsonConverter._
import com.sfxcode.nosql.mongo.operation.ObservableIncludes._
import com.typesafe.scalalogging.LazyLogging
import org.bson.BsonValue
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.{Completed, DistinctObservable, Document, MongoCollection, Observable, Observer}

abstract class Base[A]() extends LazyLogging {

  def coll: MongoCollection[Document]

  def count(filter: Bson = Document()): Observable[Long] = coll.count(filter)

  def countResult(filter: Bson = Document()): Long = count(filter).headResult()

  def distinct[S <: Any](fieldName: String, filter: Bson = Document()): DistinctObservable[BsonValue] = {
    coll.distinct[BsonValue](fieldName, filter)
  }

  def distinctResult[S <: Any](fieldName: String, filter: Bson = Document()): Seq[S] = {
    distinct(fieldName, filter).results().map(v => fromBson(v).asInstanceOf[S])
  }

  def drop(): Observable[Completed] = coll.drop()

  def dropResult(): Completed = drop().headResult()

}

class SimpleCompletedObserver[A] extends Observer[A] with LazyLogging {
  override def onError(e: Throwable): Unit = logger.error(e.getMessage, e)

  override def onComplete(): Unit = {}

  override def onNext(result: A): Unit = {}
}

