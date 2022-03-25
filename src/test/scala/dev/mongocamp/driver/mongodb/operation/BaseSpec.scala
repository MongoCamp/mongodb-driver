package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb.test.TestDatabase._
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification

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
