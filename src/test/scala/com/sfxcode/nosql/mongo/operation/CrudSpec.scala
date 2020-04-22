package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.CodecTest
import org.specs2.mutable.Specification

class CrudSpec extends Specification {

  sequential

  "Crud Operations" should {

    "create Documents in" in {
      CodecDao.drop().result()
      CodecDao.insertOne(CodecTest()).result()
      CodecDao.count().result() must be equalTo 1
      val findOneResult = CodecDao.find("id", 1).resultOption()
      findOneResult must beSome[CodecTest]
    }

  }

}
