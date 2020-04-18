package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.{ Author, Book }
import org.mongodb.scala.result.InsertOneResult
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class BaseSpec extends Specification with BeforeAll {

  sequential

  "Base Operations" should {

    "count collection size in" in {
      var insertOneResult: InsertOneResult = BookDAO.insertOne(Book.scalaBook())

      var count: Long = BookDAO.count()
      count must be equalTo 1

      val scalaBook = Book(Some(2), "Programming In Scala", 852, Author("Martin Odersky"), List(2, 4, 10))

      insertOneResult = BookDAO.insertOne(scalaBook)

      count = BookDAO.count()
      count must be equalTo 2

    }

    "must evaluate distinct" in {

      val genderList = PersonDAO.distinctResult("gender")

      genderList must have size 2
    }

    "must evaluate distinct with filter" in {

      val genderList = PersonDAO.distinctResult("gender", Map("gender" -> "male"))

      genderList must have size 1
    }

  }

  override def beforeAll: Unit = BookDAO.drop().headResult()

}
