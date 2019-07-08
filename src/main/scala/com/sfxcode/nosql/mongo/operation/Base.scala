package com.sfxcode.nosql.mongo.operation

import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Indexes.{hashed, text}
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.{CountOptions, DropIndexOptions, IndexOptions}
import org.mongodb.scala.{Completed, Document, ListIndexesObservable, MongoCollection, Observable, SingleObservable}

import scala.reflect.ClassTag

abstract class Base[A]()(implicit ct: ClassTag[A]) extends LazyLogging {

  protected def coll: MongoCollection[A]

  def count(filter: Bson = Document(), options: CountOptions = CountOptions()): Observable[Long] =
    coll.countDocuments(filter, options)

  def drop(): Observable[Completed] = coll.drop()

  def createIndexForField(field: String, sortAscending: Boolean = true,  options: IndexOptions = IndexOptions()): SingleObservable[String] =
    if (sortAscending) {
      createIndex(ascending(field), options)
    } else {
      createIndex(descending(field), options)
    }

  def createUniqueIndexForField(collectionName: String,
                        fieldName: String,
                        ascending: Boolean = true): Unit =
    createIndexForField(fieldName, ascending, IndexOptions().unique(true))

  def createIndex(key: Bson, options: IndexOptions = IndexOptions()): SingleObservable[String] =
    coll.createIndex(key, options)

  def dropIndexForName(name: String, options: DropIndexOptions = new DropIndexOptions()): SingleObservable[Completed] =
    coll.dropIndex(name, options)

  def dropIndex(keys: Bson, options: DropIndexOptions = new DropIndexOptions()): SingleObservable[Completed] = coll.dropIndex(keys, options)

  def createHashedIndex(fieldName: String,
                        options: IndexOptions = IndexOptions()): SingleObservable[String] =
    coll.createIndex(hashed(fieldName), options)

  def listIndexes: ListIndexesObservable[Map[String, Any]] = coll.listIndexes[Map[String, Any]]()

}
