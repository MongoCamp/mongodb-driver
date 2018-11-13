package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.{ Converter, _ }
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import org.mongodb.scala.result.{ DeleteResult, UpdateResult }
import org.mongodb.scala.{ Completed, Observable }

import scala.reflect.ClassTag

abstract class Crud[A]()(implicit ct: ClassTag[A]) extends Search[A] {

  // create
  def insertOne(value: A): Observable[Completed] = coll.insertOne(value)

  def insertOne(value: A, options: InsertOneOptions): Observable[Completed] =
    coll.insertOne(value, options)

  def insertMany(values: Seq[A]): Observable[Completed] =
    coll.insertMany(values)

  def insertMany(values: Seq[A], options: InsertManyOptions): Observable[Completed] =
    coll.insertMany(values, options)

  // update

  def replaceOne(value: A): Observable[UpdateResult] = {
    val document = Converter.toDocument(value)
    val oid      = document.get("_id").get
    coll.replaceOne(equal("_id", oid), value)
  }

  def replaceOne(value: A, options: ReplaceOptions): Observable[UpdateResult] = {
    val document = Converter.toDocument(value)
    val oid      = document.get("_id").get
    coll.replaceOne(equal("_id", oid), value, options)
  }

  def replaceOne(filter: Bson, value: A): Observable[UpdateResult] =
    coll.replaceOne(filter, value)

  def replaceOne(filter: Bson, value: A, options: ReplaceOptions): Observable[UpdateResult] =
    coll.replaceOne(filter, value, options)

  def updateOne(filter: Bson, update: Bson): Observable[UpdateResult] =
    coll.updateOne(filter, update)

  def updateOne(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult] =
    coll.updateOne(filter, update, options)

  def updateMany(filter: Bson, update: Bson): Observable[UpdateResult] =
    coll.updateMany(filter, update)

  def updateMany(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult] =
    coll.updateMany(filter, update, options)

  // delete

  def deleteOne(filter: Bson): Observable[DeleteResult] = coll.deleteOne(filter)

  def deleteOne(filter: Bson, options: DeleteOptions): Observable[DeleteResult] =
    coll.deleteOne(filter, options)

  def deleteOne(value: A): Observable[DeleteResult] = {
    val oid = Converter.toDocument(value).get("_id").get
    coll.deleteOne(equal("_id", oid))
  }

  def deleteMany(filter: Bson): Observable[DeleteResult] =
    coll.deleteMany(filter)

  def deleteMany(filter: Bson, options: DeleteOptions): Observable[DeleteResult] =
    coll.deleteMany(filter, options)

  def deleteAll(): Observable[DeleteResult] = deleteMany(Map())

  def deleteAll(options: DeleteOptions): Observable[DeleteResult] =
    deleteMany(Map(), options)

}
