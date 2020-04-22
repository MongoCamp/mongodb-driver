package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.database.{ ChangeObserver, MongoIndex }
import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.{ CountOptions, DropIndexOptions, IndexOptions, Indexes }
import org.mongodb.scala.{ Document, ListIndexesObservable, MongoCollection, Observable, SingleObservable }

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

abstract class Base[A]()(implicit ct: ClassTag[A]) extends LazyLogging {

  protected def coll: MongoCollection[A]

  def count(filter: Bson = Document(), options: CountOptions = CountOptions()): Observable[Long] =
    coll.countDocuments(filter, options)

  def drop(): Observable[Void] = coll.drop()

  def createIndexForField(fieldName: String,
                          sortAscending: Boolean = true,
                          options: IndexOptions = IndexOptions()): SingleObservable[String] =
    if (sortAscending) {
      createIndex(ascending(fieldName), options)
    } else {
      createIndex(descending(fieldName), options)
    }

  def createIndexForFieldWithName(fieldName: String,
                                  sortAscending: Boolean = true,
                                  name: String): SingleObservable[String] =
    createIndexForField(fieldName, sortAscending, MongoIndex.indexOptionsWithName(Some(name)))

  def createUniqueIndexForField(fieldName: String,
                                sortAscending: Boolean = true,
                                name: Option[String] = None): SingleObservable[String] =
    createIndexForField(fieldName, sortAscending, MongoIndex.indexOptionsWithName(name).unique(true))

  def createHashedIndexForField(fieldName: String, options: IndexOptions = IndexOptions()): SingleObservable[String] =
    createIndex(Indexes.hashed(fieldName), options)

  def createTextIndexForField(fieldName: String, options: IndexOptions = IndexOptions()): SingleObservable[String] =
    createIndex(Indexes.text(fieldName), options)

  def createExpiringIndexForField(fieldName: String,
                                  duration: Duration,
                                  sortAscending: Boolean = true,
                                  name: Option[String] = None): SingleObservable[String] =
    createIndexForField(fieldName,
                        sortAscending,
                        MongoIndex.indexOptionsWithName(name).expireAfter(duration._1, duration._2))

  def createIndex(key: Bson, options: IndexOptions = IndexOptions()): SingleObservable[String] =
    coll.createIndex(key, options)

  def dropIndexForName(name: String, options: DropIndexOptions = new DropIndexOptions()): SingleObservable[Void] =
    coll.dropIndex(name, options)

  def dropIndex(keys: Bson, options: DropIndexOptions = new DropIndexOptions()): SingleObservable[Void] =
    coll.dropIndex(keys, options)

  def listIndexes: ListIndexesObservable[Map[String, Any]] = coll.listIndexes[Map[String, Any]]()

  def indexList(): List[MongoIndex] = MongoIndex.convertIndexDocumentsToMongoIndexList(listIndexes)

  def hasIndexForField(fieldName: String): Boolean = MongoIndex.hasIndexForFieldWithName(listIndexes, fieldName)

}
