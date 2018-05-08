package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.Converter
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.{ DeleteResult, UpdateResult }
import org.mongodb.scala.{ Completed, Observer }

trait CrudObserver[A] extends Crud[A] {

  def insertValue(value: A, observer: Observer[Completed] = new SimpleCompletedObserver[Completed]): Unit = {
    insertOne(value).subscribe(observer)
  }

  def insertValues(values: Seq[A], observer: Observer[Completed] = new SimpleCompletedObserver[Completed]): Unit = {
    insertMany(values).subscribe(observer)
  }

  def replaceValue(value: A, observer: Observer[UpdateResult] = new SimpleCompletedObserver[UpdateResult]): Unit = {
    replaceOne(value).subscribe(observer)
  }

  def deleteValue(value: A, observer: Observer[DeleteResult] = new SimpleCompletedObserver[DeleteResult]): Unit = {
    val oid = Converter.toDocument(value).get("_id").get
    val filter = equal("_id", oid)
    deleteOne(filter)
  }

  def deleteValues(filter: Bson, observer: Observer[DeleteResult] = new SimpleCompletedObserver[DeleteResult]): Unit = {
    deleteMany(filter)
  }

}
