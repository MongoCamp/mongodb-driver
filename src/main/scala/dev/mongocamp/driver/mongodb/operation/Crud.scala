package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.sync.MongoSyncOperation
import dev.mongocamp.driver.mongodb.{ Converter, _ }
import io.circe.Decoder
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.{ DeleteResult, InsertManyResult, InsertOneResult, UpdateResult }
import org.mongodb.scala.{ BulkWriteResult, Document, Observable, SingleObservable }

import java.util.Date
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

abstract class Crud[A]()(implicit ct: ClassTag[A], decoder: Decoder[A]) extends Search[A] {

  // create
  def insertOne(value: A): Observable[InsertOneResult] = {
    coll.insertOne(Converter.toDocument(value))
  }

  def insertOne(value: A, options: InsertOneOptions): Observable[InsertOneResult] = {
    coll.insertOne(Converter.toDocument(value), options)
  }

  def insertMany(values: Seq[A]): Observable[InsertManyResult] = {
    coll.insertMany(values.map(Converter.toDocument))
  }

  def insertMany(values: Seq[A], options: InsertManyOptions): Observable[InsertManyResult] = {
    coll.insertMany(values.map(Converter.toDocument), options)
  }

  // bulk write

  def bulkWrite(requests: List[WriteModel[Document]], options: BulkWriteOptions): SingleObservable[BulkWriteResult] = {
    coll.bulkWrite(requests.map(wM => wM), options)
  }

  def bulkWrite(requests: List[WriteModel[Document]], ordered: Boolean = true): SingleObservable[BulkWriteResult] = {
    bulkWrite(requests, BulkWriteOptions().ordered(ordered))
  }

  def bulkWriteMany(values: Seq[A], options: BulkWriteOptions): SingleObservable[BulkWriteResult] = {
    val requests: ArrayBuffer[WriteModel[Document]] = ArrayBuffer()
    values.foreach(value => requests.append(InsertOneModel(Converter.toDocument(value))))
    bulkWrite(requests.toList, options)
  }

  def bulkWriteMany(values: Seq[A], ordered: Boolean = true): SingleObservable[BulkWriteResult] = {
    val requests: ArrayBuffer[WriteModel[Document]] = ArrayBuffer()
    values.foreach(value => requests.append(InsertOneModel(Converter.toDocument(value))))
    bulkWrite(requests.toList, ordered)
  }

  // update

  def replaceOne(value: A): Observable[UpdateResult] = {
    val document = Converter.toDocument(value)
    val oid      = document.get(DatabaseProvider.ObjectIdKey).get
    coll.replaceOne(equal(DatabaseProvider.ObjectIdKey, oid), document)
  }

  def replaceOne(value: A, options: ReplaceOptions): Observable[UpdateResult] = {
    val document = Converter.toDocument(value)
    val oid      = document.get(DatabaseProvider.ObjectIdKey).get
    coll.replaceOne(equal(DatabaseProvider.ObjectIdKey, oid), document, options)
  }

  def replaceOne(filter: Bson, value: A): Observable[UpdateResult] = {
    coll.replaceOne(filter, Converter.toDocument(value))
  }

  def replaceOne(filter: Bson, value: A, options: ReplaceOptions): Observable[UpdateResult] = {
    coll.replaceOne(filter, Converter.toDocument(value), options)
  }

  def updateOne(filter: Bson, update: Bson): Observable[UpdateResult] = {
    coll.updateOne(filter, update)
  }

  def updateOne(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult] = {
    coll.updateOne(filter, update, options)
  }

  def updateMany(filter: Bson, update: Bson): Observable[UpdateResult] = {
    coll.updateMany(filter, update)
  }

  def updateMany(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult] = {
    coll.updateMany(filter, update, options)
  }

  def touchInternal(filter: Bson): Observable[UpdateResult] = {
    updateMany(filter, set(MongoSyncOperation.SyncColumnLastUpdate, new Date()))
  }

  // delete

  def deleteOne(filter: Bson): Observable[DeleteResult] = {
    coll.deleteOne(filter)
  }

  def deleteOne(filter: Bson, options: DeleteOptions): Observable[DeleteResult] = {
    coll.deleteOne(filter, options)
  }

  def deleteOne(value: A): Observable[DeleteResult] = {
    val oid = Converter.toDocument(value).get(DatabaseProvider.ObjectIdKey).get
    coll.deleteOne(equal(DatabaseProvider.ObjectIdKey, oid))
  }

  def deleteMany(filter: Bson): Observable[DeleteResult] = {
    coll.deleteMany(filter)
  }

  def deleteMany(filter: Bson, options: DeleteOptions): Observable[DeleteResult] = {
    coll.deleteMany(filter, options)
  }

  def deleteAll(): Observable[DeleteResult] = {
    deleteMany(Map())
  }

  def deleteAll(options: DeleteOptions): Observable[DeleteResult] = {
    deleteMany(Map(), options)
  }

}
