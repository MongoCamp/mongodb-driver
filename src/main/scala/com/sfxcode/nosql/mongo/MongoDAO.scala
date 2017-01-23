package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.operation.Search
import org.json4s.Formats
import org.mongodb.scala.{ Document, MongoCollection }

/**
 * Created by tom on 20.01.17.
 */
abstract class MongoDAO[A](collection: MongoCollection[Document])(implicit formats: Formats, mf: Manifest[A]) extends Search[A] {

  def coll: MongoCollection[Document] = collection

}
