package dev.mongocamp.driver.mongodb.bson

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import org.bson.UuidRepresentation
import org.mongodb.scala.bson._
import org.mongodb.scala.bson.collection.mutable
import org.mongodb.scala.bson.ObjectId
import scala.collection.mutable.ArrayBuffer

class BsonConverterSuite extends munit.FunSuite {

  test("BsonConverter convert values to BSON") {
    assertEquals(BsonConverter.toBson(3), BsonInt32(3))
    assertEquals(BsonConverter.toBson(3L), BsonInt64(3))
    assertEquals(BsonConverter.toBson(3f), BsonDouble(3))
    assertEquals(BsonConverter.toBson(3d), BsonDouble(3))

    assertEquals(BsonConverter.toBson(false), BsonBoolean(false))
    assertEquals(BsonConverter.toBson(true), BsonBoolean(true))

    assertEquals(BsonConverter.toBson(java.math.BigDecimal.TEN), BsonDecimal128.apply(10))
    assertEquals(BsonConverter.toBson(BigDecimal(10)), BsonDecimal128.apply(10))
    assertEquals(BsonConverter.toBson(BigInt(10)), BsonInt64(10))
    assertEquals(BsonConverter.toBson(java.math.BigInteger.TEN), BsonInt64(10))

    assertEquals(BsonConverter.toBson(Some(5)), BsonInt32(5))

    assertEquals(BsonConverter.toBson(Some(new ObjectId("5b61455932ac3f0015ae2e7e"))), BsonObjectId("5b61455932ac3f0015ae2e7e"))

    assertEquals(BsonConverter.toBson(None), BsonNull())

    assertEquals(BsonConverter.toBson('M'), BsonString("M"))
  }

  test("convert byte, short and UUID to BSON") {
    assertEquals(BsonConverter.toBson(3.toByte), BsonInt32(3))
    assertEquals(BsonConverter.toBson(3.toShort), BsonInt32(3))
    val uuid = UUID.fromString("1c9b6e2a-3f4d-4c5e-8a7b-9d0e1f2a3b4c")
    assertEquals(BsonConverter.toBson(uuid), BsonString("1c9b6e2a-3f4d-4c5e-8a7b-9d0e1f2a3b4c"))
    assertEquals(BsonConverter.toBson(Some(uuid)), BsonString("1c9b6e2a-3f4d-4c5e-8a7b-9d0e1f2a3b4c"))
  }

  test("convert java date types to BSON date") {
    val millis = 1709198130123L
    assertEquals(BsonConverter.toBson(new Date(millis)), BsonDateTime(millis))
    assertEquals(BsonConverter.toBson(new java.sql.Date(millis)), BsonDateTime(millis))
    assertEquals(BsonConverter.toBson(new java.sql.Timestamp(millis)), BsonDateTime(millis))

    val localDate = java.time.LocalDate.of(2024, 2, 29)
    assertEquals(BsonConverter.toBson(localDate), BsonDateTime(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant.toEpochMilli))
    val localDateTime = java.time.LocalDateTime.of(2024, 2, 29, 10, 15, 30, 123000000)
    assertEquals(BsonConverter.toBson(localDateTime), BsonDateTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli))

    assertEquals(BsonConverter.toBson(Instant.ofEpochMilli(millis)), BsonDateTime(millis))
    assertEquals(BsonConverter.toBson(Instant.ofEpochMilli(millis).atZone(ZoneId.of("America/New_York"))), BsonDateTime(millis))
    assertEquals(BsonConverter.toBson(Instant.ofEpochMilli(millis).atOffset(java.time.ZoneOffset.ofHours(5))), BsonDateTime(millis))
  }

  test("convert java.time types to BSON string") {
    assertEquals(BsonConverter.toBson(java.time.LocalTime.of(10, 15, 30)), BsonString("10:15:30"))
    assertEquals(BsonConverter.toBson(java.time.OffsetTime.of(10, 15, 30, 0, java.time.ZoneOffset.ofHours(2))), BsonString("10:15:30+02:00"))
    assertEquals(BsonConverter.toBson(java.time.Duration.ofMinutes(90)), BsonString("PT1H30M"))
    assertEquals(BsonConverter.toBson(java.time.Period.of(1, 2, 3)), BsonString("P1Y2M3D"))
    assertEquals(BsonConverter.toBson(java.time.Year.of(2024)), BsonString("2024"))
    assertEquals(BsonConverter.toBson(java.time.YearMonth.of(2024, 2)), BsonString("2024-02"))
    assertEquals(BsonConverter.toBson(java.time.MonthDay.of(2, 29)), BsonString("--02-29"))
    assertEquals(BsonConverter.toBson(ZoneId.of("Europe/Berlin")), BsonString("Europe/Berlin"))
    assertEquals(BsonConverter.toBson(java.time.ZoneOffset.ofHours(-3)), BsonString("-03:00"))
  }

  test("convert joda date types to BSON date") {
    val millis = 1709198130123L
    assertEquals(BsonConverter.toBson(new org.joda.time.DateTime(millis)), BsonDateTime(millis))
    assertEquals(BsonConverter.toBson(new org.joda.time.MutableDateTime(millis)), BsonDateTime(millis))
    assertEquals(BsonConverter.toBson(new org.joda.time.Instant(millis)), BsonDateTime(millis))

    val localDate = new org.joda.time.LocalDate(2024, 2, 29)
    assertEquals(BsonConverter.toBson(localDate), BsonDateTime(localDate.toDateTimeAtStartOfDay.getMillis))
    val localDateTime = new org.joda.time.LocalDateTime(2024, 2, 29, 10, 15, 30, 123)
    assertEquals(BsonConverter.toBson(localDateTime), BsonDateTime(localDateTime.toDateTime.getMillis))
  }

  test("convert joda types to BSON string") {
    assertEquals(BsonConverter.toBson(org.joda.time.Duration.standardHours(25)), BsonString("90000000 milliseconds"))
    assertEquals(BsonConverter.toBson(new org.joda.time.LocalTime(10, 15, 30, 123)), BsonString("10:15:30.123"))
    assertEquals(BsonConverter.toBson(new org.joda.time.Period(1, 2, 0, 3, 4, 5, 6, 7)), BsonString("P1Y2M3DT4H5M6.007S"))
    assertEquals(BsonConverter.toBson(org.joda.time.DateTimeZone.forID("Europe/Berlin")), BsonString("Europe/Berlin"))
    assertEquals(BsonConverter.toBson(new org.joda.time.YearMonth(2024, 2)), BsonString("2024-02"))
    assertEquals(BsonConverter.toBson(new org.joda.time.MonthDay(2, 29)), BsonString("--02-29"))
  }

  test("convert UUID from BSON binary") {
    val uuid = UUID.fromString("1c9b6e2a-3f4d-4c5e-8a7b-9d0e1f2a3b4c")
    assertEquals(BsonConverter.fromBson(new BsonBinary(uuid)), uuid)
    assertEquals(BsonConverter.fromBson(new BsonBinary(uuid, UuidRepresentation.JAVA_LEGACY)), uuid)
    assertEquals(BsonConverter.fromBson(BsonString(uuid.toString)), uuid.toString)
    assertEquals(BsonConverter.fromBson(BsonBinary(Array[Byte](1, 2, 3))).asInstanceOf[Array[Byte]].toList, List[Byte](1, 2, 3))
  }

  test("convert Map to BSON") {
    assertEquals(BsonConverter.toBson(Map("test" -> 1)).isInstanceOf[org.bson.BsonDocument], true)
    assertEquals(BsonConverter.toBson(scala.collection.mutable.Map("test" -> 1)).isInstanceOf[org.bson.BsonDocument], true)
  }

  test("convert List to BSON") {
    assertEquals(BsonConverter.toBson(List("test")).isInstanceOf[org.bson.BsonArray], true)
    val buffer = new ArrayBuffer[String]()
    buffer.+=("Test")
    assertEquals(BsonConverter.toBson(buffer).isInstanceOf[org.bson.BsonArray], true)
  }

  test("convert values from BSON") {
    assertEquals(BsonConverter.fromBson(BsonInt32(3)), 3)
    assertEquals(BsonConverter.fromBson(BsonInt64(3)), 3L)
    assertEquals(BsonConverter.fromBson(BsonDouble(3)), 3.0)
  }

  test("evaluate dot notation") {
    val document: mutable.Document = mutable.Document()
    val secondLevelDocument        = mutable.Document()
    secondLevelDocument.put("test", 42)
    document.put("secondLevelDocument", secondLevelDocument)

    assertEquals(document.get("secondLevelDocument").isDefined, true)
    assertEquals(document.get("secondLevelDocument.test").isEmpty, true)

    val v = BsonConverter.documentValueOption(Document(document.toJson()), "secondLevelDocument.test")
    assertEquals(v.isDefined, true)

  }

  test("evaluate get with dot notation") {
    val document: mutable.Document = mutable.Document()
    val secondLevelDocument        = mutable.Document()
    secondLevelDocument.put("test", 42)
    document.put("secondLevelDocument", secondLevelDocument)

    assertEquals(document.get("secondLevelDocument").isDefined, true)
    assertEquals(document.get("secondLevelDocument.test"), None)

    val v = BsonConverter.documentValueOption(Document(document.toJson()), "secondLevelDocument.test")

    assertEquals(v.isDefined, true)
  }

  test("evaluate put with dot notation") {
    val document = Document()

    var updated: Document = BsonConverter.updateDocumentValue(document, "test", 42)

    assertEquals(updated.getIntValue("test"), 42)

    updated = BsonConverter.updateDocumentValue(document, "test.test.test.test", 42)

    assertEquals(updated.getIntValue("test.test.test.test"), 42)
  }

}
