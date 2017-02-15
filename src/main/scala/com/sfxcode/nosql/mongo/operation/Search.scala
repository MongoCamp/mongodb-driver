package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.operation.ObservableIncludes._
import org.json4s.Formats
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{ Document, FindObservable, MongoCollection }

abstract class Search[A]()(implicit formats: Formats, mf: Manifest[A]) extends Crud[A] {

  def coll: MongoCollection[Document]

  def findByObservable(observable: FindObservable[Document], limit: Int = 0, maxWait: Int = 10): List[A] = observable.limit(limit).resultList(maxWait)

  def findOneByObservable(observable: FindObservable[Document], maxWait: Int = 10): Option[A] = observable.result(maxWait)

  def find(filter: Bson = Document(), limit: Int = 0, maxWait: Int = 10): List[A] = findByObservable(coll.find(filter), limit, maxWait)

  def findOne(filter: Bson = Document()): Option[A] = findOneByObservable(coll.find(filter))

  def findAll(limit: Int = 0, maxWait: Int = 10): List[A] = find(Document(), limit, maxWait)

  def findOneById(oid: Any): Option[A] = {
    oid match {
      case objectId: ObjectId => findOne(equal("_id", objectId))
      case _ => None
    }
  }

  def find(name: String, value: Any): List[A] = find(equal(name, value))

  def findOne(name: String, value: Any): Option[A] = findOne(equal(name, value))

  def findAggregated(aggregator: Seq[Bson], maxWait: Int = 10): List[A] = coll.aggregate(aggregator).resultList(maxWait)

}
