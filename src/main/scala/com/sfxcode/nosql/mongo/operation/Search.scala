package com.sfxcode.nosql.mongo.operation

import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{ AggregateObservable, Document, FindObservable, MongoCollection }

import scala.reflect.ClassTag

abstract class Search[A]()(implicit ct: ClassTag[A]) extends Crud[A] {

  protected def coll: MongoCollection[A]

  def find(filter: Bson = Document(), sort: Bson = Document(), projection: Bson = Document()): FindObservable[A] = coll.find(filter).sort(sort).projection(projection)

  def findById(oid: Any): FindObservable[A] = {
    oid match {
      case objectId: ObjectId => find(equal("_id", objectId))
      case _ => find(equal("_id", new ObjectId(oid.toString)))
    }
  }

  def find(name: String, value: Any): FindObservable[A] = find(equal(name, value))

  def findAggregated(aggregator: Seq[Bson]): AggregateObservable[A] = coll.aggregate(aggregator)

}
