package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.tour.Database._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.Person
import org.specs2.mutable.{ Before, Specification }

class SearchSpec extends Specification with Before {

  sequential

  "Search Operations" should {

    "support findAll" in {

      val findAllResult = PersonDAO.findAll()

      findAllResult.size must be equalTo PersonDAO.countResult().toInt

      findAllResult.head.name must not beEmpty

      findAllResult.head._id.toString must not beEmpty

    }

    "support findOneById" in {

      val findAllResult = PersonDAO.findAll()

      val findOneByIdResult = PersonDAO.findOneById(findAllResult.head._id)

      findOneByIdResult must beSome[Person]

      findOneByIdResult.get must be equalTo findAllResult.head
    }

    "support findOne with Filter" in {

      val findOneResult = PersonDAO.findOne(Map("id" -> 11))

      findOneResult must beSome[Person]

      findOneResult.get.name must be equalTo "Dyer Mayer"

      PersonDAO.findOne(Map("id" -> 125)).get.name must be equalTo "Gaines Valentine"
    }

    "support findOne with field name and value" in {

      val findOneResult = PersonDAO.findOne("id", 11)

      findOneResult must beSome[Person]

      findOneResult.get.name must be equalTo "Dyer Mayer"

      PersonDAO.findOne("name", "Gaines Valentine").get.name must be equalTo "Gaines Valentine"
    }

    "support findOne with Filter" in {

      val females = PersonDAO.find(Map("gender" -> "female"))

      females.size must be equalTo 98

      val males = PersonDAO.find(Map("gender" -> "male"))

      males.size must be equalTo 102

    }

  }

  override def before: Any = printDatabaseStatus()
}
