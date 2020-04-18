package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.operation.Crud
import org.mongodb.scala.{ Document, MongoCollection }

import scala.reflect.ClassTag

/**
  * Created by tom on 20.01.17.
  */
abstract class MongoDAO[A](provider: DatabaseProvider, collectionName: String)(implicit ct: ClassTag[A])
    extends Crud[A] {

  val collection: MongoCollection[A] = {
    if (collectionName.contains(DatabaseProvider.CollectionSeparator)) {
      val newDatabaseName   = collectionName.substring(0, collectionName.indexOf(DatabaseProvider.CollectionSeparator))
      val newCollectionName = collectionName.substring(collectionName.indexOf(DatabaseProvider.CollectionSeparator) + 1)
      provider.database(newDatabaseName).getCollection[A](newCollectionName)
    } else {
      provider.database().getCollection[A](collectionName)
    }
  }

  protected def coll: MongoCollection[A] = collection

  // internal object for raw document access
  object Raw extends MongoDAO[Document](provider, collectionName)

}
