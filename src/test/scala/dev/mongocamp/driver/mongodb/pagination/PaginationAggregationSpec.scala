package dev.mongocamp.driver.mongodb.pagination

import dev.mongocamp.driver.mongodb.Aggregate._
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
// #agg_imports

import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{ filter, group, sort }
import org.mongodb.scala.model.Filters.{ and, equal }

class PaginationAggregationSpec extends PersonSpecification {

  val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

  val groupStage: Bson = group(Map("age" -> "$age"), sumField("balance"), firstField("age"))

  val sortStage: Bson = sort(sortByKey("age"))

  "Search" should {

    "support aggregation filter" in {

      // #region aggregation-pagination
      val pipeline = List(filterStage, sortStage)

      val pagination = MongoPaginatedAggregation(PersonDAO.Raw, pipeline, allowDiskUse = true)

      val page = pagination.paginate(1, 10)
      // #endregion aggregation-pagination

      (pagination.countResult must be).equalTo(98)

      (page.paginationInfo.allCount must be).equalTo(98)

      (page.paginationInfo.pagesCount must be).equalTo(10)

      (page.databaseObjects.size must be).equalTo(10)
    }

    "support aggregation filter and group" in {
      // #agg_execute
      val pipeline = List(filterStage, groupStage, sortStage)

      val pagination = MongoPaginatedAggregation(PersonDAO, pipeline, allowDiskUse = true)

      val page = pagination.paginate(1, 10)

      (pagination.countResult must be).equalTo(21)

      (page.paginationInfo.allCount must be).equalTo(21)

      (page.paginationInfo.pagesCount must be).equalTo(3)

      (page.databaseObjects.size must be).equalTo(10)

      // #agg_convert
      val list: List[Map[String, Any]] = page.databaseObjects.map(d => BsonConverter.asMap(d))
      // #agg_convert
      list.foreach(m => println(m("age").toString + " -> " + m("balance")))

      (list.head("age") must be).equalTo(20)
      (list.head("balance") must be).equalTo(8333.0)

    }

    "aggregation with empty response" in {
      val pipeline = List(filter(and(equal("unknown", "filter"))), groupStage, sortStage)

      val pagination = MongoPaginatedAggregation(PersonDAO, pipeline, allowDiskUse = true)

      val page = pagination.paginate(1, 10)

      (pagination.countResult must be).equalTo(0)

      (page.paginationInfo.allCount must be).equalTo(0)

      (page.paginationInfo.pagesCount must be).equalTo(0)

      (page.databaseObjects.size must be).equalTo(0)


    }

  }

}
