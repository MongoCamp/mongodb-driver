package dev.mongocamp.driver.mongodb.pagination

import dev.mongocamp.driver.mongodb.Aggregate._
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{ filter, group, sort }
import org.mongodb.scala.model.Filters.{ and, equal }

class PaginationAggregationSuite extends BasePersonSuite {

  val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

  val groupStage: Bson = group(Map("age" -> "$age"), sumField("balance"), firstField("age"))

  val sortStage: Bson = sort(sortByKey("age"))

  test("support aggregation filter") {

    // #region aggregation-pagination
    val pipeline = List(filterStage, sortStage)

    val pagination = MongoPaginatedAggregation(PersonDAO.Raw, pipeline, allowDiskUse = true)

    val page = pagination.paginate(1, 10)
    // #endregion aggregation-pagination

    assertEquals(pagination.countResult, 98L)

    assertEquals(page.paginationInfo.allCount, 98L)

    assertEquals(page.paginationInfo.pagesCount, 10L)

    assertEquals(page.databaseObjects.size, 10)
  }

  test("support aggregation filter and group") {
    // #agg_execute
    val pipeline = List(filterStage, groupStage, sortStage)

    val pagination = MongoPaginatedAggregation(PersonDAO, pipeline, allowDiskUse = true)

    val page = pagination.paginate(1, 10)

    assertEquals(pagination.countResult, 21L)

    assertEquals(page.paginationInfo.allCount, 21L)

    assertEquals(page.paginationInfo.pagesCount, 3L)

    assertEquals(page.databaseObjects.size, 10)

    // #agg_convert
    val list: List[Map[String, Any]] = page.databaseObjects.map(d => BsonConverter.asMap(d))
    // #agg_convert
    list.foreach(m => println(m("age").toString + " -> " + m("balance")))

    assertEquals(list.head("age"), 20)
    assertEquals(list.head("balance"), 8333.0)
  }

  test("aggregation with empty response") {
    val pipeline = List(filter(and(equal("unknown", "filter"))), groupStage, sortStage)

    val pagination = MongoPaginatedAggregation(PersonDAO, pipeline, allowDiskUse = true)

    val page = pagination.paginate(1, 10)

    assertEquals(pagination.countResult, 0L)

    assertEquals(page.paginationInfo.allCount, 0L)

    assertEquals(page.paginationInfo.pagesCount, 0L)

    assertEquals(page.databaseObjects.size, 0)
  }

}
