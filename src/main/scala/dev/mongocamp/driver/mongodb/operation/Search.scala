package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.bson.BsonConverter._
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import io.circe.Decoder
import net.sf.jsqlparser.statement.select.Skip
import org.bson.BsonValue
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{DistinctObservable, Document, MongoCollection, Observable}

import scala.reflect.ClassTag

abstract class Search[A]()(implicit ct: ClassTag[A], decoder: Decoder[A]) extends Base[A] {

  protected def coll: MongoCollection[Document]

  def find(
      filter: Bson = Document(),
      sort: Bson = Document(),
      projection: Bson = Document(),
      limit: Int = 0,
      skip: Int = 0
  ): Observable[A] = {
    val findObservable = {
      if (limit > 0) {
        coll.find(filter).sort(sort).projection(projection).limit(limit).skip(skip)
      }
      else {
        coll.find(filter).sort(sort).projection(projection).skip(skip)
      }
    }
    findObservable.map(doc => documentToObject[A](doc, decoder))
  }

  def findById(oid: ObjectId): Observable[A] = {
    find(equal(DatabaseProvider.ObjectIdKey, oid))
  }

  def find(name: String, value: Any): Observable[A] = {
    find(equal(name, value))
  }

  def distinct[S <: Any](fieldName: String, filter: Bson = Document()): DistinctObservable[BsonValue] = {
    coll.distinct[BsonValue](fieldName, filter)
  }

  def distinctResult[S <: Any](fieldName: String, filter: Bson = Document()): Seq[S] = {
    distinct(fieldName, filter).resultList().map(v => fromBson(v).asInstanceOf[S])
  }

  def findAggregated(pipeline: Seq[Bson], allowDiskUse: Boolean = false): Observable[A] = {
    val aggregateObservable = coll.aggregate(pipeline).allowDiskUse(allowDiskUse)
    aggregateObservable.map {
      case a: A =>
        a
      case doc =>
        documentToObject[A](doc, decoder)
    }
  }

}
