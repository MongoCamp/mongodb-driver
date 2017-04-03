package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.{ Converter, _ }
import org.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.result.{ DeleteResult, UpdateResult }
import org.mongodb.scala.{ Completed, Observable, Observer }

import scala.reflect.ClassTag

abstract class Crud[A]()(implicit ct: ClassTag[A]) extends Base[A] {

  def insertOne(value: A): Observable[Completed] = coll.insertOne(value)

  def insert(value: A, observer: Observer[Completed] = new SimpleCompletedObserver[Completed]): Unit = {
    insertOne(value).subscribe(observer)
  }

  def insertResult(value: A): Completed = insertOne(value)

  def insertMany(values: Seq[A]): Observable[Completed] = coll.insertMany(values)

  def insertValues(values: Seq[A], observer: Observer[Completed] = new SimpleCompletedObserver[Completed]): Unit = {
    insertMany(values).subscribe(observer)
  }

  def insertValuesResult(values: Seq[A]): Completed = insertMany(values)

  def replaceOne(value: A): Observable[UpdateResult] = {
    val document = Converter.toDocument(value)
    val oid = document.get("_id").get
    coll.replaceOne(equal("_id", oid), value)
  }

  def update(value: A, observer: Observer[UpdateResult] = new SimpleCompletedObserver[UpdateResult]): Unit = {
    replaceOne(value).subscribe(observer)
  }

  def updateResult(value: A): UpdateResult = replaceOne(value)

  def deleteOne(filter: Bson): Observable[DeleteResult] = coll.deleteOne(filter)

  def deleteResult(filter: Bson): DeleteResult = deleteOne(filter)

  def delete(filter: Bson, observer: Observer[DeleteResult] = new SimpleCompletedObserver[DeleteResult]): Unit = {
    deleteOne(filter)
  }

  def deleteOne(value: A): Observable[DeleteResult] = {
    val oid = Converter.toDocument(value).get("_id").get
    coll.deleteOne(equal("_id", oid))
  }

  def deleteByValueResult(value: A): DeleteResult = deleteOne(value)

  def deleteByValue(value: A, observer: Observer[DeleteResult] = new SimpleCompletedObserver[DeleteResult]): Unit = {
    val oid = Converter.toDocument(value).get("_id").get
    val filter = equal("_id", oid)
    deleteOne(filter)
  }

  def deleteMany(filter: Bson): Observable[DeleteResult] = coll.deleteMany(filter)

  def deleteManyResult(filter: Bson): DeleteResult = deleteMany(filter)

  def deleteAll(): Observable[DeleteResult] = deleteMany(Map())

  def deleteAllResult(): DeleteResult = deleteAll()

}
