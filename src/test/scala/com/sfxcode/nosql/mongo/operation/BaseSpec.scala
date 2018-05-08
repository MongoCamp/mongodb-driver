package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.{ Author, Book }
import TestDatabase._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class BaseSpec extends Specification with BeforeAll {

  sequential

  "Base Operations" should {

    "count collection size in" in {
      BookDAO.insertResult(Book.scalaBook())

      BookDAO.countResult() must be equalTo 1

      val scalaBook = Book(Some(2), "Programming In Scala", 852, Author("Martin Odersky"), List(2, 4, 10))

      BookDAO.insertResult(scalaBook)

      BookDAO.countResult() must be equalTo 2

    }

    "must evaluate distinct" in {

      val genderList = PersonDAO.distinctResult("gender")

      genderList must have size 2
    }

    "must evaluate distinct with filter" in {

      val genderList = PersonDAO.distinctResult("gender", Map("gender" -> "male"))

      genderList must have size 1
    }

    "must create / drop indexes for key" in {

      val createIndexResult = PersonDAO.createIndexForFieldResult("name")

      createIndexResult must be equalTo "name_1"

      PersonDAO.createIndexForFieldResult("name") must be equalTo "name_1"

      PersonDAO.dropIndexForNameResult("name_1") must not beNull

    }

  }

  override def beforeAll: Unit = BookDAO.dropResult()

}
