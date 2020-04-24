package com.sfxcode.nosql.mongo.dao

import com.sfxcode.nosql.mongo.TestDatabase.PersonDAO
import com.sfxcode.nosql.mongo._

class PersonDAOSpec extends PersonSpecification {

  "PersonDAO" should {

    "support count" in {
      val count: Long = PersonDAO.count().result()
      count mustEqual 200
    }
  }
}
