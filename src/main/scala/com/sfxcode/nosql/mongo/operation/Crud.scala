package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.{ Converter, _ }
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import org.mongodb.scala.result.{ DeleteResult, UpdateResult }
import org.mongodb.scala.{ Completed, Observable }

import scala.reflect.ClassTag

abstract class Crud[A]()(implicit ct: ClassTag[A]) extends Base[A] {

  // create
  def insertOne(value: A): Observable[Completed] = coll.insertOne(value)

  def insertOneResult(value: A): Completed = coll.insertOne(value)

  def insertOne(value: A, options: InsertOneOptions): Observable[Completed] = coll.insertOne(value, options)

  def insertOneResult(value: A, options: InsertOneOptions): Completed = insertOne(value, options)

  def insertMany(values: Seq[A]): Observable[Completed] = coll.insertMany(values)

  def insertManyResult(values: Seq[A]): Completed = insertMany(values)

  def insertMany(values: Seq[A], options: InsertManyOptions): Observable[Completed] = coll.insertMany(values, options)

  def insertManyResult(values: Seq[A], options: InsertManyOptions): Completed = insertMany(values, options)

  // update

  def replaceOne(value: A): Observable[UpdateResult] = {
    val document = Converter.toDocument(value)
    val oid = document.get("_id").get
    coll.replaceOne(equal("_id", oid), value)
  }

  def replaceOneResult(value: A): UpdateResult = replaceOne(value)

  def replaceOne(value: A, options: ReplaceOptions): Observable[UpdateResult] = {
    val document = Converter.toDocument(value)
    val oid = document.get("_id").get
    coll.replaceOne(equal("_id", oid), value, options)
  }

  def replaceOneResult(value: A, options: ReplaceOptions): UpdateResult = replaceOne(value, options)

  def replaceOne(filter: Bson, value: A): Observable[UpdateResult] =
    coll.replaceOne(filter, value)

  def replaceOneResult(filter: Bson, value: A): UpdateResult = replaceOne(filter, value)

  def replaceOne(filter: Bson, value: A, options: ReplaceOptions): Observable[UpdateResult] =
    coll.replaceOne(filter, value, options)

  def replaceOneResult(filter: Bson, value: A, options: ReplaceOptions): UpdateResult = replaceOne(filter, value, options)

  def updateOne(filter: Bson, update: Bson): Observable[UpdateResult] =
    coll.updateOne(filter, update)

  def updateOneResult(filter: Bson, update: Bson): UpdateResult =
    updateOne(filter, update)

  def updateOne(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult] =
    coll.updateOne(filter, update, options)

  def updateOneResult(filter: Bson, update: Bson, options: UpdateOptions): UpdateResult =
    updateOne(filter, update, options)

  def updateMany(filter: Bson, update: Bson): Observable[UpdateResult] =
    coll.updateMany(filter, update)

  def updateManyResult(filter: Bson, update: Bson): UpdateResult =
    updateMany(filter, update)

  def updateMany(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult] =
    coll.updateMany(filter, update, options)

  def updateManyResult(filter: Bson, update: Bson, options: UpdateOptions): UpdateResult =
    updateMany(filter, update, options)

  // delete

  def deleteOne(filter: Bson): Observable[DeleteResult] = coll.deleteOne(filter)

  def deleteOneResult(filter: Bson): DeleteResult = deleteOne(filter)

  def deleteOne(filter: Bson, options: DeleteOptions): Observable[DeleteResult] = coll.deleteOne(filter, options)

  def deleteOneResult(filter: Bson, options: DeleteOptions): DeleteResult = deleteOne(filter, options)

  def deleteOne(value: A): Observable[DeleteResult] = {
    val oid = Converter.toDocument(value).get("_id").get
    coll.deleteOne(equal("_id", oid))
  }

  def deleteOneResult(value: A): DeleteResult = deleteOne(value)

  def deleteMany(filter: Bson): Observable[DeleteResult] = coll.deleteMany(filter)

  def deleteManyResult(filter: Bson): DeleteResult = deleteMany(filter)

  def deleteMany(filter: Bson, options: DeleteOptions): Observable[DeleteResult] = coll.deleteMany(filter, options)

  def deleteManyResult(filter: Bson, options: DeleteOptions): DeleteResult = deleteMany(filter, options)

  def deleteAll(): Observable[DeleteResult] = deleteMany(Map())

  def deleteAllResult(): DeleteResult = deleteAll()

  def deleteAll(options: DeleteOptions): Observable[DeleteResult] = deleteMany(Map(), options)

  def deleteAllResult(options: DeleteOptions): DeleteResult = deleteAll(options)

}
