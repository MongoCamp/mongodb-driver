package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.bson.BsonConverter._
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import org.bson.BsonValue
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{ AggregateObservable, DistinctObservable, Document, FindObservable, MongoCollection }

import scala.reflect.ClassTag

abstract class Search[A]()(implicit ct: ClassTag[A]) extends Base[A] {

  protected def coll: MongoCollection[A]

  def find(
      filter: Bson = Document(),
      sort: Bson = Document(),
      projection: Bson = Document(),
      limit: Int = 0
  ): FindObservable[A] =
    if (limit > 0) {
      coll.find(filter).sort(sort).projection(projection).limit(limit)
    }
    else {
      coll.find(filter).sort(sort).projection(projection)
    }

  def findById(oid: ObjectId): FindObservable[A] = find(equal(DatabaseProvider.ObjectIdKey, oid))

  def find(name: String, value: Any): FindObservable[A] =
    find(equal(name, value))

  def distinct[S <: Any](fieldName: String, filter: Bson = Document()): DistinctObservable[BsonValue] =
    coll.distinct[BsonValue](fieldName, filter)

  def distinctResult[S <: Any](fieldName: String, filter: Bson = Document()): Seq[S] =
    distinct(fieldName, filter).resultList().map(v => fromBson(v).asInstanceOf[S])

  def findAggregated(pipeline: Seq[Bson], allowDiskUse: Boolean = false): AggregateObservable[A] =
    coll.aggregate(pipeline).allowDiskUse(allowDiskUse)

}
