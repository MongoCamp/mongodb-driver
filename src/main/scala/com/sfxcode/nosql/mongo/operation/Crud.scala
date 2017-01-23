package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.Converter
import com.sfxcode.nosql.mongo.operation.ObservableIncludes._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.result.{ DeleteResult, UpdateResult }
import org.mongodb.scala.{ Completed, Document, MongoCollection }

/**
 * Created by tom on 20.01.17.
 */
abstract class Crud[A]() extends Base[A] {

  def coll: MongoCollection[Document]

  def insert(value: A): Unit = coll.insertOne(Converter.toDocument(value)).subscribe(new SimpleCompletedObserver[Completed] {})

  def insertResult(value: A): Completed = coll.insertOne(Converter.toDocument(value)).headResult()

  def insert(seq: Seq[A]): Unit = coll.insertMany(seq.map(value => Converter.toDocument(value))).
    subscribe(new SimpleCompletedObserver[Completed] {})

  def insertResult(seq: Seq[A]): Completed = coll.insertMany(seq.map(value => Converter.toDocument(value))).headResult()

  def update(value: A): Unit = {
    val document = Converter.toDocument(value)
    val oid = document.get("_id").get
    coll.replaceOne(equal("_id", oid), document).subscribe(new SimpleCompletedObserver[UpdateResult] {})
  }

  def updateResult(value: A): UpdateResult = {
    val document = Converter.toDocument(value)
    val oid = document.get("_id").get
    coll.replaceOne(equal("_id", oid), document).headResult()
  }

  def delete(value: A): Unit = {
    val oid = Converter.toDocument(value).get("_id").get
    coll.deleteOne(equal("_id", oid)).subscribe(new SimpleCompletedObserver[DeleteResult] {})
  }

  def deleteResult(value: A): DeleteResult = {
    val oid = Converter.toDocument(value).get("_id").get
    coll.deleteOne(equal("_id", oid)).headResult()
  }

}
