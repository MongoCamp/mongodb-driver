package com.sfxcode.nosql

import java.util.Date

import com.sfxcode.nosql.mongo.Converter
import com.sfxcode.nosql.mongo.bson.BsonConverter
import com.sfxcode.nosql.mongo.bson.convert.JsonDateTimeConverter
import com.sfxcode.nosql.mongo.database.MongoConfig
import com.sfxcode.nosql.mongo.gridfs.GridFSStreamObserver
import com.sfxcode.nosql.mongo.operation.ObservableIncludes
import org.bson.BsonValue
import org.bson.json.{JsonMode, JsonWriterSettings}
import org.bson.types.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.gridfs.{GridFSFile, GridFSFindObservable}
import org.mongodb.scala.{Document, FindObservable, Observable, ObservableImplicits}

import scala.collection.JavaConverters._
import scala.language.implicitConversions

package object mongo extends MongoIncludes with DocumentIncludes {

  implicit class DocumentExtensions[A <: Document](val document: A) extends AnyVal {

    def asPlainMap: Map[String, Any] =
      BsonConverter.asMap(document)

    def asPlainJson: String = {
      val builder = JsonWriterSettings.builder
        .dateTimeConverter(new JsonDateTimeConverter())
        .outputMode(JsonMode.RELAXED)
      document.toJson(builder.build())
    }

    def getValueOption(key: String): Option[Any] = BsonConverter.documentValueOption(document, key)

    def getValue(key: String): Any = getValueOption(key).orNull

    def getStringValue(key: String): String =
      getValue(key) match {
        case n: Any => n.toString
        case _      => ""
      }

    def getLongValue(key: String): Long = {
      val value = getValue(key)
      value match {
        case n: Number => n.longValue()
        case s: String => s.toLongOption.getOrElse(0)
        case _         => 0
      }
    }

    def getIntValue(key: String): Int = getLongValue(key).intValue()

    def getDoubleValue(key: String): Double =
      getValue(key) match {
        case n: Number => n.doubleValue()
        case s: String => s.toDoubleOption.getOrElse(0)
        case _         => 0
      }

    def getDateValue(key: String): Date = {
      val value = getValue(key)
      value match {
        case date: Date => date
        case _          => null
      }
    }

    def getFloatValue(key: String): Float = getDoubleValue(key).floatValue()

    def updateValue(key: String, value: Any): Any = BsonConverter.updateDocumentValue(document, key, value)
  }
}

trait MongoIncludes extends ObservableIncludes with ObservableImplicits {
  implicit def stringToConfig(database: String): MongoConfig = MongoConfig(database)

  implicit def observableToResult[T](obs: Observable[T]): T = obs.result()

  implicit def findObservableToResultList[T](obs: FindObservable[T]): List[T] =
    obs.resultList()

  implicit def findObservableToResultOption[T](obs: FindObservable[T]): Option[T] = obs.resultOption()

  // gridfs

  implicit def gridFSFindObservableToFiles(observable: GridFSFindObservable): List[GridFSFile] =
    observable.resultList()

  implicit def gridFSFileToObjectId(file: GridFSFile): ObjectId =
    file.getObjectId

  implicit def gridFSFileToBSonIdValue(file: GridFSFile): BsonValue = file.getId

  implicit def observerToResultLength(observer: GridFSStreamObserver): Long = {
    while (!observer.completed.get) {}
    observer.resultLength.get()
  }

}

trait DocumentIncludes {
  implicit def mapToBson(value: Map[_, _]): Bson = Converter.toDocument(value)

  implicit def documentFromJavaMap(map: java.util.Map[String, Any]): Document =
    documentFromScalaMap(map.asScala.toMap)

  implicit def documentFromMutableMap(map: collection.mutable.Map[String, Any]): Document =
    documentFromScalaMap(map.toMap)

  implicit def documentFromScalaMap(map: Map[String, Any]): Document = {
    var result = Document()
    map.keys.foreach { key =>
      val v = map.getOrElse(key, null)
      result.+=(key -> BsonConverter.toBson(v))
    }
    result
  }

  implicit def documentFromDocument(doc: org.bson.Document): Document = {
    var result = Document()
    doc
      .keySet()
      .asScala
      .foreach { key =>
        val v = doc.get(key)
        result.+=(key -> BsonConverter.toBson(v))
      }
    result
  }

  implicit def mapFromDocument(document: Document): Map[String, Any] =
    BsonConverter.asMap(document)

  implicit def mapListFromDocuments(documents: List[Document]): List[Map[String, Any]] =
    BsonConverter.asMapList(documents)

  // ObjectId
  implicit def stringToObjectId(str: String): ObjectId = new ObjectId(str)

  implicit def documentToObjectId(doc: Document): ObjectId =
    doc.getObjectId("_id")
}
