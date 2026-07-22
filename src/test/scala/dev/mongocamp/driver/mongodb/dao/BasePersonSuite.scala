package dev.mongocamp.driver.mongodb.dao

import better.files.Resource
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.test.TestDatabase._

abstract class BasePersonSuite extends munit.FunSuite {

  override def beforeAll(): Unit = {
    PersonDAO.drop().result()
    PersonDAO.importJsonFile(Resource.getUrl("json/people.json")).result()
  }

}
