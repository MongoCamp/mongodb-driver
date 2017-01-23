package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.bson.BsonConverter._
import com.sfxcode.nosql.mongo.operation.ObservableIncludes._
import com.typesafe.scalalogging.LazyLogging
import org.bson.BsonValue
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.{ Completed, Document, MongoCollection, Observer }
/**
 * Created by tom on 20.01.17.
 */
abstract class Base[A]() extends LazyLogging {

  def coll: MongoCollection[Document]

  def count(filter: Bson = Document()): Long = coll.count(filter).headResult()

  def distinct[S <: Any](fieldName: String, filter: Bson = Document()): Seq[S] = coll.distinct[BsonValue](fieldName, filter).results().map(v => fromBson(v).asInstanceOf[S])

  def drop(): Unit = coll.drop().subscribe(new SimpleCompletedObserver[Completed] {})
  def dropResult(): Completed = coll.drop().headResult()

}

class SimpleCompletedObserver[A] extends Observer[A] with LazyLogging {
  override def onError(e: Throwable): Unit = logger.error(e.getMessage, e)

  override def onComplete(): Unit = {}

  override def onNext(result: A): Unit = {}
}

