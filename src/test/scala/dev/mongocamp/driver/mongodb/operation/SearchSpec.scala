package dev.mongocamp.driver.mongodb.operation
import dev.mongocamp.driver.MongoImplicits
import dev.mongocamp.driver.mongodb.Sort._
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.model.Person
import dev.mongocamp.driver.mongodb.test.TestDatabase._

class SearchSpec extends PersonSpecification with MongoImplicits {

  "Search Operations" should {

    "support findAll" in {

      val findAllResult: List[Person] = PersonDAO.find()

      (findAllResult.size must be).equalTo(PersonDAO.count().result().toInt)

      findAllResult.head.name must not be null
      (findAllResult.head.name must not).equalTo("")

      findAllResult.head._id.toString must not be null
      (findAllResult.head._id.toString must not).equalTo("")
    }

    "support findOneById" in {

      val findAllResult: List[Person] = PersonDAO.find()

      val findOneByIdResult: Option[Person] = PersonDAO.findById(findAllResult.head._id)

      findOneByIdResult must beSome[Person]

      (findOneByIdResult.get must be).equalTo(findAllResult.head)
    }

    "support findOne with Filter" in {

      val findOneResult = PersonDAO.find(Map("id" -> 11)).resultOption()

      findOneResult must beSome[Person]

      (findOneResult.get.name must be).equalTo("Dyer Mayer")

      (PersonDAO.find(Map("id" -> 125)).result().name must be).equalTo("Gaines Valentine")
    }

    "support findOne with field name and value" in {

      val findOneResult = PersonDAO.find("id", 11).resultOption()

      findOneResult must beSome[Person]

      (findOneResult.get.name must be).equalTo("Dyer Mayer")

      (PersonDAO.find("name", "Gaines Valentine").result().name must be).equalTo("Gaines Valentine")
    }

    "support findOne with Filter" in {

      val females = PersonDAO.find(Map("gender" -> "female"), sortByKey("name")).resultList()

      (females.size must be).equalTo(98)

      val males = PersonDAO.find(Map("gender" -> "male")).resultList()

      (males.size must be).equalTo(102)

    }

  }

}
