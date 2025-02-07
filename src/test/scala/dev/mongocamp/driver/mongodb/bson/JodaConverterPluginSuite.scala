package dev.mongocamp.driver.mongodb.bson

import org.joda.time.DateTime

import scala.concurrent.duration.Duration

class JodaConverterPluginSuite extends munit.FunSuite {

  test("convert joda dates to bson dates") {
    val dateTime      = new DateTime("2023-11-02")
    val bsonDocument  = BsonConverter.toBson(dateTime)
    val roundTripDate = new DateTime(bsonDocument.asDateTime().getValue)
    assertEquals(roundTripDate, dateTime)
  }

  test("convert joda duration to bson string") {
    val duration     = org.joda.time.Duration.standardDays(1)
    val bsonDocument = BsonConverter.toBson(duration)
    assertEquals(bsonDocument.toString, "BsonString{value='86400000ms'}")
    assertEquals(Duration("86400000ms").toMillis, duration.getMillis)
  }

  override def beforeAll(): Unit = {
    BsonConverter.converterPlugin = new JodaConverterPlugin()
  }

  override def afterAll(): Unit = {
    BsonConverter.converterPlugin = new BaseConverterPlugin()
  }
}