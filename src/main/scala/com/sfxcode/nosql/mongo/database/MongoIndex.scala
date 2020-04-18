package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo.operation.ObservableIncludes
import org.mongodb.scala.ListIndexesObservable
import org.mongodb.scala.model.IndexOptions

case class MongoIndex(name: String, key: String, ascending: Int, version: Int, namespace: String, keys:Map[String, Any] = Map())

object MongoIndex extends ObservableIncludes {

  def indexOptionsWithName(name: Option[String]): IndexOptions = {
    if (name.isDefined) {
      IndexOptions().name(name.get)
    } else {
      IndexOptions()
    }
  }

  def hasIndexForFieldWithName(listIndexesObservable: ListIndexesObservable[Map[String, Any]],
                               fieldName: String,
                               maxWait: Int = DefaultMaxWait): Boolean =
    convertIndexDocumentsToMongoIndexList(listIndexesObservable, maxWait).exists(index => index.key == fieldName)

  def convertIndexDocumentsToMongoIndexList(listIndexesObservable: ListIndexesObservable[Map[String, Any]],
                                            maxWait: Int = DefaultMaxWait): List[MongoIndex] = {
    var result = List[MongoIndex]()
    try {
      result = listIndexesObservable
        .resultList(maxWait)
        .map(
          indexOptions =>
            MongoIndex(
              indexOptions("name").toString, {
                if (indexOptions.contains("textIndexVersion")) {
                  indexOptions("name").toString.substring(0, indexOptions("name").toString.indexOf("_"))
                } else {
                  indexOptions("key").asInstanceOf[Map[String, _]].head.asInstanceOf[(String, Int)]._1
                }
              }, {
                if (indexOptions.contains("textIndexVersion")) {
                  indexOptions("textIndexVersion").toString.toInt
                } else {
                  val value = indexOptions("key").asInstanceOf[Map[String, _]].head._2.toString
                  if (value.matches("[0-9]")) {
                    value.toInt
                  } else {
                    0
                  }
                }
              },
              indexOptions("v").toString.toInt,
              indexOptions("ns").toString,
              indexOptions("key").asInstanceOf[Map[String, _]]
          )
        )
    }
    result
  }

}
