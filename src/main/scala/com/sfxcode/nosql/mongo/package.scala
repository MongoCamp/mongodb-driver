package com.sfxcode.nosql

import com.sfxcode.nosql.mongo.bson.BsonConverter
import com.sfxcode.nosql.mongo.operation.ObservableIncludes
import org.mongodb.scala.Document
import org.mongodb.scala.bson.conversions.Bson
import scala.language.implicitConversions

import scala.collection.JavaConverters._

package object mongo extends ObservableIncludes {

  implicit def mapToBson(value: Map[_, _]): Bson = Converter.toDocument(value)

  implicit def documentFromJavaMap(map: java.util.Map[String, Any]): Document = {
    documentFromScalaMap(map.asScala.toMap)
  }

  implicit def documentFromMutableMap(map: collection.mutable.Map[String, Any]): Document = {
    documentFromScalaMap(map.toMap)
  }

  implicit def documentFromScalaMap(map: Map[String, Any]): Document = {
    var result = Document()
    map.keys.foreach(key => {
      val v = map.getOrElse(key, null)
      result.+=(key -> BsonConverter.toBson(v))
    })
    result
  }

  implicit def documentFromDocument(doc: org.bson.Document): Document = {
    var result = Document()
    doc.keySet().asScala.foreach(key => {
      val v = doc.get(key)
      result.+=(key -> BsonConverter.toBson(v))
    })
    result
  }

}