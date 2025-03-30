package dev.mongocamp.driver.mongodb.operation

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.Converter
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.result.InsertManyResult
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.Observer

trait CrudObserver[A] extends Crud[A] {

  def insertValue(value: A, observer: Observer[InsertOneResult] = new SimpleObserver[InsertOneResult]): Unit =
    insertOne(value).subscribe(observer)

  def insertValues(values: Seq[A], observer: Observer[InsertManyResult] = new SimpleObserver[InsertManyResult]): Unit =
    insertMany(values).subscribe(observer)

  def replaceValue(value: A, observer: Observer[UpdateResult] = new SimpleObserver[UpdateResult]): Unit =
    replaceOne(value).subscribe(observer)

  def deleteValue(value: A, observer: Observer[DeleteResult] = new SimpleObserver[DeleteResult]): Unit = {
    val oid    = Converter.toDocument(value).get(DatabaseProvider.ObjectIdKey).get
    val filter = equal(DatabaseProvider.ObjectIdKey, oid)
    deleteOne(filter).subscribe(observer)
  }

  def deleteValues(filter: Bson, observer: Observer[DeleteResult] = new SimpleObserver[DeleteResult]): Unit =
    deleteMany(filter).subscribe(observer)

}
