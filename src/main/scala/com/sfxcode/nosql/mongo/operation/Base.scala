package com.sfxcode.nosql.mongo.operation

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.{ CountOptions, IndexOptions }
import org.mongodb.scala.{ Completed, Document, MongoCollection, Observable, SingleObservable }

import scala.reflect.ClassTag

abstract class Base[A]()(implicit ct: ClassTag[A]) extends LazyLogging {

  protected def coll: MongoCollection[A]

  def count(filter: Bson = Document(), options: CountOptions = CountOptions()): Observable[Long] =
    coll.countDocuments(filter, options)

  def drop(): Observable[Completed] = coll.drop()

  def createIndexForField(field: String, sortAscending: Boolean = true): SingleObservable[String] =
    if (sortAscending) {
      createIndex(ascending(field))
    } else {
      createIndex(descending(field))
    }

  def createIndex(key: Bson, options: IndexOptions = IndexOptions()): SingleObservable[String] =
    coll.createIndex(key, options)

  def dropIndexForName(name: String): SingleObservable[Completed] =
    coll.dropIndex(name)

  def dropIndex(keys: Bson): SingleObservable[Completed] = coll.dropIndex(keys)

}
