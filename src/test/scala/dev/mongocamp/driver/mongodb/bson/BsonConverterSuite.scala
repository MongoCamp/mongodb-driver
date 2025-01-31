package dev.mongocamp.driver.mongodb.bson

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import org.mongodb.scala.bson.collection.mutable
import org.mongodb.scala.bson.{ ObjectId, _ }

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
