package com.sfxcode.nosql.mongo.bson

import org.bson.BsonValue
import org.joda.time.DateTime
import org.mongodb.scala.bson.{BsonDateTime, BsonNull}

class JodaConverterPlugin extends AbstractConverterPlugin {
  override def customClassList: List[Class[_]] = List(classOf[DateTime])

  override def toBson(value: Any): BsonValue =
    value match {
      case dt: DateTime => BsonDateTime(dt.toDate)
      case _ =>
        BsonNull()

    }
}
