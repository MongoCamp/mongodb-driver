package dev.mongocamp.driver.mongodb.bson

import org.bson.BsonValue
import org.joda.time.{ DateTime, Duration }
import org.mongodb.scala.bson.{ BsonDateTime, BsonNull, BsonString }

class JodaConverterPlugin extends AbstractConverterPlugin {
  override def customClassList: List[Class[_]] = List(classOf[DateTime], classOf[Duration])

  override def toBson(value: Any): BsonValue =
    value match {
      case dt: DateTime =>
        BsonDateTime(dt.toDate)
      case dt: org.joda.time.Duration =>
        BsonString(s"${dt.getMillis}ms")
      case _ =>
        BsonNull()

    }
}
