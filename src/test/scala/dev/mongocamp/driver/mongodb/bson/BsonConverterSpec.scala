package dev.mongocamp.driver.mongodb.bson

import org.mongodb.scala.bson.collection.mutable
import org.mongodb.scala.bson.{ObjectId, _}
import org.specs2.mutable.Specification

import scala.collection.mutable.ArrayBuffer
import dev.mongocamp.driver.mongodb._

/** Created by tom on 22.01.17.
  */
class BsonConverterSpec extends Specification {

  sequential

  "BsonConverter" should {

    "convert values to BSON" in {
      BsonConverter.toBson(3) must be equalTo BsonInt32(3)
      BsonConverter.toBson(3L) must be equalTo BsonInt64(3)
      BsonConverter.toBson(3f) must be equalTo BsonDouble(3)
      BsonConverter.toBson(3d) must be equalTo BsonDouble(3)

      BsonConverter.toBson(false) must be equalTo BsonBoolean(false)
      BsonConverter.toBson(true) must be equalTo BsonBoolean(true)

      BsonConverter.toBson(java.math.BigDecimal.TEN) must be equalTo BsonDecimal128.apply(10)
      BsonConverter.toBson(BigDecimal(10)) must be equalTo BsonDecimal128.apply(10)
      BsonConverter.toBson(BigInt(10)) must be equalTo BsonInt64(10)
      BsonConverter.toBson(java.math.BigInteger.TEN) must be equalTo BsonInt64(10)

      BsonConverter.toBson(Some(5)) must be equalTo BsonInt32(5)

      BsonConverter.toBson(Some(new ObjectId("5b61455932ac3f0015ae2e7e"))) must be equalTo BsonObjectId(
        "5b61455932ac3f0015ae2e7e"
      )

      BsonConverter.toBson(None) must be equalTo BsonNull()

      BsonConverter.toBson('M') must be equalTo BsonString("M")
    }

    "convert Map to BSON" in {
      BsonConverter.toBson(Map(("test" -> 1))) must beAnInstanceOf[org.bson.BsonDocument]
      BsonConverter.toBson(scala.collection.mutable.Map(("test" -> 1))) must beAnInstanceOf[org.bson.BsonDocument]
    }

    "convert List to BSON" in {
      BsonConverter.toBson(List(("test"))) must beAnInstanceOf[org.bson.BsonArray]
      val buffer = new ArrayBuffer[String]()
      buffer.+=("Test")
      BsonConverter.toBson(buffer) must beAnInstanceOf[org.bson.BsonArray]
    }

    "convert values from BSON" in {
      BsonConverter.fromBson(BsonInt32(3)) must be equalTo 3
      BsonConverter.fromBson(BsonInt64(3)) must be equalTo 3L

      BsonConverter.fromBson(BsonDouble(3)) must be equalTo 3.0

    }

    "evaluate dot notation" in {
      val document: mutable.Document = mutable.Document()
      val secondLevelDocument        = mutable.Document()
      secondLevelDocument.put("test", 42)
      document.put("secondLevelDocument", secondLevelDocument)

      document.get("secondLevelDocument") must beSome

      document.get("secondLevelDocument.test") must beNone

      val v = BsonConverter.documentValueOption(Document(document.toJson()), "secondLevelDocument.test")

      true must beTrue
    }

    "evaluate get with dot notation" in {
      val document: mutable.Document = mutable.Document()
      val secondLevelDocument        = mutable.Document()
      secondLevelDocument.put("test", 42)
      document.put("secondLevelDocument", secondLevelDocument)

      document.get("secondLevelDocument") must beSome

      document.get("secondLevelDocument.test") must beNone

      val v = BsonConverter.documentValueOption(Document(document.toJson()), "secondLevelDocument.test")

      true must beTrue
    }

    "evaluate put with dot notation" in {
      val document = Document()

      var updated: Document = BsonConverter.updateDocumentValue(document, "test", 42)

      updated.getIntValue("test") mustEqual (42)

      updated = BsonConverter.updateDocumentValue(document, "test.test.test.test", 42)

      updated.getIntValue("test.test.test.test") mustEqual 42
      true must beTrue
    }

  }

}
