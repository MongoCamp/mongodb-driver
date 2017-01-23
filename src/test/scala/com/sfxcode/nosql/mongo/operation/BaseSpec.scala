package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.Database._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.{ Author, Book }
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

/**
 * Created by tom on 22.01.17.
 */
class BaseSpec extends Specification with BeforeAll {

  sequential

  "Base Operations" should {

    "count collection size in" in {
      BookDAO.insertResult(Book.scalaBook)

      BookDAO.count() must be equalTo 1

      val scalaBook = Book(Some(2), "Programming In Scala", 852, Author("Martin Odersky"), Set(2, 4, 10))

      BookDAO.insertResult(scalaBook)

      BookDAO.count() must be equalTo 2

    }

    "must evaluate distinct" in {

      val genderList = PersonDAO.distinct("gender")

      genderList must have size 2
    }

    "must evaluate distinct with filter" in {

      val genderList = PersonDAO.distinct("gender", Map("gender" -> "male"))

      genderList must have size 1
    }

  }

  override def beforeAll = BookDAO.drop()

}
