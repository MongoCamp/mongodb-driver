package com.sfxcode.nosql.mongo.dao

import better.files.{ File, Resource }
import com.sfxcode.nosql.mongo.TestDatabase.{ BookDAO, PersonDAO }
import com.sfxcode.nosql.mongo._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class BookDAOSpec extends Specification with BeforeAll {
  sequential

  override def beforeAll(): Unit = {
    BookDAO.drop().result()
    BookDAO.importJsonFile(File(Resource.getUrl("json/books.json"))).result()
  }

  "BookDAO" should {
    "support count" in {
      val count: Long = BookDAO.count().result()
      count mustEqual 431
    }
  }
}
