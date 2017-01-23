package com.sfxcode.nosql.mongo.operation

import org.json4s.Formats
import org.mongodb.scala.{ Document, FindObservable, MongoCollection }
import ObservableIncludes._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._

/**
 * Created by tom on 20.01.17.
 */
abstract class Search[A]()(implicit formats: Formats, mf: Manifest[A]) extends Crud[A] {

  def coll: MongoCollection[Document]

  def findByObservable(observable: FindObservable[Document]): List[A] = observable.resultList()

  def findOneByObservable(observable: FindObservable[Document]): Option[A] = observable.result()

  def find(filter: Bson = Document()): List[A] = findByObservable(coll.find(filter))

  def findOne(filter: Bson = Document()): Option[A] = findOneByObservable(coll.find(filter))

  def findAll(): List[A] = find()

  def findOneById(oid: Any): Option[A] = {
    oid match {
      case objectId: ObjectId => findOne(equal("_id", objectId))
      case _ => None
    }
  }

  def find(name: String, value: Any): List[A] = find(equal(name, value))

  def findOne(name: String, value: Any): Option[A] = findOne(equal(name, value))

}
