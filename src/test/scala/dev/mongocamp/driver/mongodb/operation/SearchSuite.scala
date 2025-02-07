package dev.mongocamp.driver.mongodb.operation
import dev.mongocamp.driver.MongoImplicits
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.Sort._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.model.Person
import dev.mongocamp.driver.mongodb.test.TestDatabase._

class SearchSuite extends BasePersonSuite with MongoImplicits {

  test("support findAll") {
    val findAllResult: List[Person] = PersonDAO.find()
    assertEquals(findAllResult.size, PersonDAO.count().result().toInt)
    assert(findAllResult.head.name != null)
    assert(findAllResult.head.name != "")
    assert(findAllResult.head._id.toString != null)
    assert(findAllResult.head._id.toString != "")
  }

  test("support findOneById") {
    val findAllResult: List[Person]       = PersonDAO.find()
    val findOneByIdResult: Option[Person] = PersonDAO.findById(findAllResult.head._id)
    assert(findOneByIdResult.isDefined)
    assertEquals(findOneByIdResult.get, findAllResult.head)
  }

  test("support findOne with Filter") {
    val findOneResult = PersonDAO.find(Map("id" -> 11)).resultOption()
    assert(findOneResult.isDefined)
    assertEquals(findOneResult.get.name, "Dyer Mayer")
    assertEquals(PersonDAO.find(Map("id" -> 125)).result().name, "Gaines Valentine")
  }

  test("support findOne with field name and value") {
    val findOneResult = PersonDAO.find("id", 11).resultOption()
    assert(findOneResult.isDefined)
    assertEquals(findOneResult.get.name, "Dyer Mayer")
    assertEquals(PersonDAO.find("name", "Gaines Valentine").result().name, "Gaines Valentine")
  }

  test("support many with Filter") {
    val females = PersonDAO.find(Map("gender" -> "female"), sortByKey("name")).resultList()
    assertEquals(females.size, 98)
    val males = PersonDAO.find(Map("gender" -> "male")).resultList()
    assertEquals(males.size, 102)
  }

}
