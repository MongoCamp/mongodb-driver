package dev.mongocamp.driver.mongodb.operation

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.database.MongoIndex
import dev.mongocamp.driver.mongodb.schema.{ CirceSchema, JsonConverter }
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.{ CountOptions, DropIndexOptions, IndexOptions, Indexes }
import org.mongodb.scala.{ Document, ListIndexesObservable, MongoCollection, Observable, SingleObservable }

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.duration.durationToPair
import scala.reflect.ClassTag
import io.circe.generic.auto._
import io.circe.syntax._
import better.files.Resource
import io.circe.{ Decoder, HCursor }
import io.circe.jawn.decode
import io.circe.syntax._
import io.circe.generic.auto._

abstract class Base[A](implicit classTag: ClassTag[A]) extends LazyLogging {
  def jsonConverter = new JsonConverter()
  def documentToObject[A](document: Document)(implicit decoder: Decoder[A]): A = {
    if (classTag.runtimeClass == classOf[Document]) {
      document.asInstanceOf[A]
    }
    else {
      val helperMap = mutable.Map[String, Any]()
      document.keys.foreach(k => helperMap.put(k, BsonConverter.fromBson(document(k))))
      val jsonString = jsonConverter.toJson(helperMap)
      val response   = jsonConverter.toObject(jsonString)
//      val decoder         = converter.decodeString[A](jsonString)
      response
    }
  }

  protected def coll: MongoCollection[Document]

  def count(filter: Bson = Document(), options: CountOptions = CountOptions()): Observable[Long] = {
    coll.countDocuments(filter, options)
  }

  def drop(): Observable[Unit] = coll.drop()

  def createIndexForField(fieldName: String, sortAscending: Boolean = true, options: IndexOptions = IndexOptions()): SingleObservable[String] = {
    if (sortAscending) {
      createIndex(ascending(fieldName), options)
    }
    else {
      createIndex(descending(fieldName), options)
    }
  }

  def createIndexForFieldWithName(fieldName: String, sortAscending: Boolean = true, name: String): SingleObservable[String] = {
    createIndexForField(fieldName, sortAscending, MongoIndex.indexOptionsWithName(Some(name)))
  }

  def createUniqueIndexForField(fieldName: String, sortAscending: Boolean = true, name: Option[String] = None): SingleObservable[String] = {
    createIndexForField(fieldName, sortAscending, MongoIndex.indexOptionsWithName(name).unique(true))
  }

  def createHashedIndexForField(fieldName: String, options: IndexOptions = IndexOptions()): SingleObservable[String] = {
    createIndex(Indexes.hashed(fieldName), options)
  }

  def createTextIndexForField(fieldName: String, options: IndexOptions = IndexOptions()): SingleObservable[String] = {
    createIndex(Indexes.text(fieldName), options)
  }

  def createExpiringIndexForField(
      fieldName: String,
      duration: Duration,
      sortAscending: Boolean = true,
      name: Option[String] = None
  ): SingleObservable[String] = {
    createIndexForField(fieldName, sortAscending, MongoIndex.indexOptionsWithName(name).expireAfter(duration._1, duration._2))
  }

  def createIndex(key: Bson, options: IndexOptions = IndexOptions()): SingleObservable[String] = coll.createIndex(key, options)

  def dropIndexForName(name: String, options: DropIndexOptions = new DropIndexOptions()): SingleObservable[Unit] = {
    coll.dropIndex(name, options)
  }

  def dropIndex(keys: Bson, options: DropIndexOptions = new DropIndexOptions()): SingleObservable[Unit] = {
    coll.dropIndex(keys, options)
  }

  def listIndexes: ListIndexesObservable[Map[String, Any]] = coll.listIndexes[Map[String, Any]]()

  def indexList(): List[MongoIndex] = MongoIndex.convertIndexDocumentsToMongoIndexList(listIndexes)

  def indexForName(name: String): Option[MongoIndex] = indexList().find(_.name.equals(name))

  def hasIndexForField(fieldName: String): Boolean = indexList().exists(index => index.fields.contains(fieldName))

}
