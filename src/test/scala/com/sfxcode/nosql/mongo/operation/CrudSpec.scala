package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.dao.PersonSpecification
import com.sfxcode.nosql.mongo.model.CodecTest
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class CrudSpec extends PersonSpecification {

  sequential

  override def beforeAll(): Unit = {
    super.beforeAll()
    CodecDao.drop().result()
    CodecDao.insertOne(CodecTest()).result()
  }

  "Crud Operations" should {

    "create Documents in" in {

      CodecDao.count().result() must be equalTo 1
      val findOneResult = CodecDao.find("id", 1).resultOption()
      findOneResult must beSome[CodecTest]
    }

  }

}
