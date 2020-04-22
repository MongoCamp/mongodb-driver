package com.sfxcode.nosql.mongo.dao

import better.files.{ File, Resource }
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.TestDatabase.PersonDAO
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class PersonDAOSpec extends PersonSpecification {

  "PersonDAO" should {

    "support count" in {
      val count: Long = PersonDAO.count().result()
      count mustEqual 200
    }
  }
}
