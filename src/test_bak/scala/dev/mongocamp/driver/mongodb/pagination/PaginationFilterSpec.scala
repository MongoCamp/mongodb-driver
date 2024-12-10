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

      val personCollectionCount = PersonDAO.count().result().toInt
      page.paginationInfo.allCount mustEqual personCollectionCount
      pagination.countResult mustEqual personCollectionCount

      page.databaseObjects.size must beEqualTo(10)

      page.databaseObjects.head.name must not be null
      (page.databaseObjects.head.name must not).equalTo("")

      page.databaseObjects.head._id must not be null
      (page.databaseObjects.head._id must not).equalTo("")

    }

    "support with Filter" in {
      // #region filter-pagination
      val paginationFemale = MongoPaginatedFilter(PersonDAO, Map("gender" -> "female"), sortByKey("name"))

      val pageFemale = paginationFemale.paginate(1, 10)
      // #endregion filter-pagination

      paginationFemale.countResult mustEqual 98
      pageFemale.paginationInfo.pagesCount mustEqual 10
      pageFemale.paginationInfo.allCount mustEqual 98
      pageFemale.paginationInfo.page mustEqual 1
      pageFemale.paginationInfo.perPage mustEqual 10

      pageFemale.databaseObjects.size mustEqual 10
      pageFemale.databaseObjects.head.name mustEqual "Adele Melton"

      val paginationMales = MongoPaginatedFilter(PersonDAO, Map("gender" -> "male"))
      val pageMale        = paginationMales.paginate(1, 10)

      paginationMales.countResult mustEqual 102
      pageMale.paginationInfo.pagesCount mustEqual 11
      pageMale.paginationInfo.allCount mustEqual 102
      pageMale.paginationInfo.page mustEqual 1
      pageMale.paginationInfo.perPage mustEqual 10

      pageMale.databaseObjects.size mustEqual 10
      pageMale.databaseObjects.head.name mustEqual "Bowen Leon"

    }

  }

}
