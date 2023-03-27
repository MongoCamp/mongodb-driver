package dev.mongocamp.driver.mongodb.pagination

import dev.mongocamp.driver.MongoImplicits
import dev.mongocamp.driver.mongodb.Sort._
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase._

class PaginationFilterSpec extends PersonSpecification with MongoImplicits {

  "Search Operations" should {

    "support findAll" in {

      val pagination = MongoPaginatedFilter(PersonDAO)

      val page = pagination.paginate(1, 10)

      (page.paginationInfo.allCount must be).equalTo(PersonDAO.count().result().toInt)

      page.databaseObjects.size must beEqualTo(10)

      page.databaseObjects.head.name must not beEmpty

      page.databaseObjects.head._id.toString must not beEmpty

    }

    "support with Filter" in {
      val paginationFemale = MongoPaginatedFilter(PersonDAO, Map("gender" -> "female"), sortByKey("name"))

      val pageFemale = paginationFemale.paginate(1, 10)

      pageFemale.paginationInfo.pagesCount mustEqual 10
      pageFemale.paginationInfo.allCount mustEqual 98
      pageFemale.paginationInfo.page mustEqual 1
      pageFemale.paginationInfo.perPage mustEqual 10

      pageFemale.databaseObjects.size mustEqual 10
      pageFemale.databaseObjects.head.name mustEqual "Adele Melton"

      val paginationMales = MongoPaginatedFilter(PersonDAO, Map("gender" -> "male"))
      val pageMale        = paginationMales.paginate(1, 10)

      pageMale.paginationInfo.pagesCount mustEqual 11
      pageMale.paginationInfo.allCount mustEqual 102
      pageMale.paginationInfo.page mustEqual 1
      pageMale.paginationInfo.perPage mustEqual 10

      pageMale.databaseObjects.size mustEqual 10
      pageMale.databaseObjects.head.name mustEqual "Bowen Leon"

    }

  }

}
