package dev.mongocamp.driver.mongodb.dao

import better.files.{ File, Resource }
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

abstract class PersonSpecification extends Specification with BeforeAll {

  sequential

  override def beforeAll(): Unit = {
    PersonDAO.drop().result()
    PersonDAO.importJsonFile(File(Resource.getUrl("json/people.json"))).result()
  }

}
