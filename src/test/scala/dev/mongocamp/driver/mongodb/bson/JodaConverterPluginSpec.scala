package dev.mongocamp.driver.mongodb.bson

import org.joda.time.DateTime
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

import scala.concurrent.duration.Duration

class JodaConverterPluginSpec extends Specification with BeforeAfterAll {

  sequential

  "JodaConverterPlugin" should {

    "convert joda dates to bson dates" in {
      val dateTime     = new DateTime("2023-11-02")
      val bsonDocument = BsonConverter.toBson(dateTime)
      (bsonDocument.toString must be).equalTo("BsonDateTime{value=1698879600000}")
    }

    "convert joda duration to bson string" in {
      val duration     = org.joda.time.Duration.standardDays(1)
      val bsonDocument = BsonConverter.toBson(duration)
      (bsonDocument.toString must be).equalTo("BsonString{value='86400000ms'}")
      (Duration("86400000ms").toMillis must be).equalTo(duration.getMillis)
    }

  }

  override def beforeAll(): Unit = {
    BsonConverter.converterPlugin = new JodaConverterPlugin()
  }
  override def afterAll(): Unit = {
    BsonConverter.converterPlugin = new BaseConverterPlugin()
  }
}
