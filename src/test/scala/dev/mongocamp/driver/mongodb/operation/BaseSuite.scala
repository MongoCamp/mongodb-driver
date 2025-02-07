package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._

class BaseSuite extends BasePersonSuite {

  test("must evaluate distinct") {
    val genderList = PersonDAO.distinctResult("gender")
    assertEquals(genderList.size, 2)
  }

  test("must evaluate distinct with filter") {
    val genderList = PersonDAO.distinctResult("gender", Map("gender" -> "male"))
    assertEquals(genderList.size, 1)
  }

  override def beforeAll(): Unit = BookDAO.drop().result()

}
