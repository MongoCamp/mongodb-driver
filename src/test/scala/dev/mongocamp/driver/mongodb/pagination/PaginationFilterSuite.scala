package dev.mongocamp.driver.mongodb.pagination
import dev.mongocamp.driver.MongoImplicits
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.Sort._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._

class PaginationFilterSuite extends BasePersonSuite with MongoImplicits {

  test("support findAll") {

    val pagination = MongoPaginatedFilter(PersonDAO)

    val page = pagination.paginate(1, 10)

    val personCollectionCount = PersonDAO.count().result().toInt
    assertEquals(page.paginationInfo.allCount, personCollectionCount.toLong)
    assertEquals(pagination.countResult, personCollectionCount.toLong)

    assertEquals(page.databaseObjects.size, 10)

    assertNotEquals(page.databaseObjects.head.name, null)
    assertNotEquals(page.databaseObjects.head.name, "")

    assertNotEquals(page.databaseObjects.head._id, null)
    assertNotEquals(page.databaseObjects.head._id.toHexString, "")

  }

  test("support with Filter") {
    // #region filter-pagination
    val paginationFemale = MongoPaginatedFilter(PersonDAO, Map("gender" -> "female"), sortByKey("name"))

    val pageFemale = paginationFemale.paginate(1, 10)
    // #endregion filter-pagination

    assertEquals(paginationFemale.countResult, 98L)
    assertEquals(pageFemale.paginationInfo.pagesCount, 10L)
    assertEquals(pageFemale.paginationInfo.allCount, 98L)
    assertEquals(pageFemale.paginationInfo.page, 1L)
    assertEquals(pageFemale.paginationInfo.perPage, 10L)

    assertEquals(pageFemale.databaseObjects.size, 10)
    assertEquals(pageFemale.databaseObjects.head.name, "Adele Melton")

    val paginationMales = MongoPaginatedFilter(PersonDAO, Map("gender" -> "male"))
    val pageMale        = paginationMales.paginate(1, 10)

    assertEquals(paginationMales.countResult, 102L)
    assertEquals(pageMale.paginationInfo.pagesCount, 11L)
    assertEquals(pageMale.paginationInfo.allCount, 102L)
    assertEquals(pageMale.paginationInfo.page, 1L)
    assertEquals(pageMale.paginationInfo.perPage, 10L)

    assertEquals(pageMale.databaseObjects.size, 10)
    assertEquals(pageMale.databaseObjects.head.name, "Bowen Leon")

  }

}
