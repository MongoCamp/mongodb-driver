package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.Book
import org.mongodb.scala.result.InsertOneResult
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class BaseSpec extends Specification with BeforeAll {

  sequential

  "Base Operations" should {

    "must evaluate distinct" in {

      val genderList = PersonDAO.distinctResult("gender")

      genderList must have size 2
    }

    "must evaluate distinct with filter" in {

      val genderList = PersonDAO.distinctResult("gender", Map("gender" -> "male"))

      genderList must have size 1
    }

  }

  override def beforeAll: Unit = BookDAO.drop().result()

}
