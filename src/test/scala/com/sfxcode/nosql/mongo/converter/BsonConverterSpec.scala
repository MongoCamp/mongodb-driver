package com.sfxcode.nosql.mongo.converter

import com.sfxcode.nosql.mongo.bson.BsonConverter
import org.mongodb.scala.bson._
import org.specs2.mutable.Specification

/**
 * Created by tom on 22.01.17.
 */
class BsonConverterSpec extends Specification {

  sequential

  "BsonConverter" should {

    "convert values to BSON" in {
      BsonConverter.toBson(3) must be equalTo BsonInt32(3)
      BsonConverter.toBson(3l) must be equalTo BsonInt64(3)
      BsonConverter.toBson(3f) must be equalTo BsonDouble(3)
      BsonConverter.toBson(3d) must be equalTo BsonDouble(3)

      BsonConverter.toBson(false) must be equalTo BsonBoolean(false)
      BsonConverter.toBson(true) must be equalTo BsonBoolean(true)

      BsonConverter.toBson(java.math.BigDecimal.TEN) must be equalTo BsonDouble(10)
      BsonConverter.toBson(BigDecimal(10)) must be equalTo BsonDouble(10)
      BsonConverter.toBson(BigInt(10)) must be equalTo BsonInt64(10)
      BsonConverter.toBson(java.math.BigInteger.TEN) must be equalTo BsonInt64(10)

    }

    "convert values from BSON" in {
      BsonConverter.fromBson(BsonInt32(3)) must be equalTo 3
      BsonConverter.fromBson(BsonInt64(3)) must be equalTo 3l

      BsonConverter.fromBson(BsonDouble(3)) must be equalTo 3.0

    }

  }

}
