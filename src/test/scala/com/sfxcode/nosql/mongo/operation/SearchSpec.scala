package com.sfxcode.nosql.mongo.operation

import better.files.{ File, Resource }
import com.sfxcode.nosql.mongo.Sort._
import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.database.DatabaseSpec
import com.sfxcode.nosql.mongo.model.Person
import org.specs2.specification.BeforeAll

class SearchSpec extends DatabaseSpec with BeforeAll {

  override def beforeAll(): Unit = {
    PersonDAO.drop().result()
    PersonDAO.importJsonFile(File(Resource.getUrl("json/people.json"))).result()
  }

  "Search Operations" should {

    "support findAll" in {

      val findAllResult: List[Person] = PersonDAO.find()

      findAllResult.size must be equalTo PersonDAO.count().result().toInt

      findAllResult.head.name must not beEmpty

      findAllResult.head._id.toString must not beEmpty

    }

    "support findOneById" in {

      val findAllResult: List[Person] = PersonDAO.find()

      val findOneByIdResult: Option[Person] = PersonDAO.findById(findAllResult.head._id)

      findOneByIdResult must beSome[Person]

      findOneByIdResult.get must be equalTo findAllResult.head
    }

    "support findOne with Filter" in {

      val findOneResult = PersonDAO.find(Map("id" -> 11)).resultOption()

      findOneResult must beSome[Person]

      findOneResult.get.name must be equalTo "Dyer Mayer"

      PersonDAO.find(Map("id" -> 125)).result().name must be equalTo "Gaines Valentine"
    }

    "support findOne with field name and value" in {

      val findOneResult = PersonDAO.find("id", 11).resultOption()

      findOneResult must beSome[Person]

      findOneResult.get.name must be equalTo "Dyer Mayer"

      PersonDAO.find("name", "Gaines Valentine").result().name must be equalTo "Gaines Valentine"
    }

    "support findOne with Filter" in {

      val females = PersonDAO.find(Map("gender" -> "female"), sortByKey("name")).resultList()

      females.size must be equalTo 98

      val males = PersonDAO.find(Map("gender" -> "male")).resultList()

      males.size must be equalTo 102

    }

  }

}
