package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.operation.Crud
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}

import scala.reflect.ClassTag

/**
  * Created by tom on 20.01.17.
  */
abstract class MongoDAO[A](database: MongoDatabase, collectionName: String)(
    implicit ct: ClassTag[A])
    extends Crud[A] {

  val collection: MongoCollection[A] = database.getCollection[A](collectionName)

  protected def coll: MongoCollection[A] = collection

  // internal object for raw document access
  object Raw extends MongoDAO[Document](database, collectionName)

}
