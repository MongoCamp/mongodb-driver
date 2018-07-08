package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.bson.BsonConverter._
import org.bson.BsonValue
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{ AggregateObservable, DistinctObservable, Document, FindObservable, MongoCollection }

import scala.reflect.ClassTag

abstract class Search[A]()(implicit ct: ClassTag[A]) extends Base[A] {

  protected def coll: MongoCollection[A]

  def find(filter: Bson = Document(), sort: Bson = Document(), projection: Bson = Document()): FindObservable[A] = coll.find(filter).sort(sort).projection(projection)

  def findById(oid: ObjectId): FindObservable[A] = find(equal("_id", oid))

  def find(name: String, value: Any): FindObservable[A] = find(equal(name, value))

  def distinct[S <: Any](fieldName: String, filter: Bson = Document()): DistinctObservable[BsonValue] = {
    coll.distinct[BsonValue](fieldName, filter)
  }

  def distinctResult[S <: Any](fieldName: String, filter: Bson = Document()): Seq[S] = {
    distinct(fieldName, filter).results().map(v => fromBson(v).asInstanceOf[S])
  }

  def findAggregated(aggregator: Seq[Bson]): AggregateObservable[A] = coll.aggregate(aggregator)

}
