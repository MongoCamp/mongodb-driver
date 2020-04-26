package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.test.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.dao.PersonSpecification

class BaseSpec extends PersonSpecification {

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
