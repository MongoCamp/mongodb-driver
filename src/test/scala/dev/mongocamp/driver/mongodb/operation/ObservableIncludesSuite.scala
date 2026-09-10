package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import scala.concurrent.duration.Duration
import scala.concurrent.duration.SECONDS

class ObservableIncludesSuite extends BasePersonSuite {

  test("DefaultMaxWaitDuration should default to the reference.conf value of 10 seconds") {
    assertEquals(ObservableIncludes.DefaultMaxWaitDuration, Duration(10, SECONDS))
  }

  test("result should accept an Int and a Duration in addition to the zero-arg overload") {
    val person = PersonDAO.find().result()
    assert(person != null)
    assertEquals(PersonDAO.find().result(30), person)
    assertEquals(PersonDAO.find().result(Duration(30, SECONDS)), person)
  }

  test("resultList should accept an Int and a Duration in addition to the zero-arg overload") {
    val people = PersonDAO.find().resultList()
    assert(people.nonEmpty)
    assertEquals(PersonDAO.find().resultList(30), people)
    assertEquals(PersonDAO.find().resultList(Duration(30, SECONDS)), people)
  }
}
