package dev.mongocamp.driver.mongodb.pagination

// #agg_imports
import dev.mongocamp.driver.mongodb.Aggregate._
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
// #agg_imports

import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{filter, group, sort}
import org.mongodb.scala.model.Filters.{and, equal}

class PaginationAggregationSpec extends PersonSpecification {

  // #agg_stages
  val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

  val groupStage: Bson = group(Map("age" -> "$age"), sumField("balance"), firstField("age"))

  val sortStage: Bson = sort(sortByKey("age"))
  // #agg_stages

  "Search" should {

    "support aggregation filter" in {

      val pipeline = List(filterStage, sortStage)

      val pagination = MongoPaginatedAggregation(PersonDAO.Raw, pipeline, allowDiskUse = true)

      val page = pagination.paginate(1, 10)

      (page.paginationInfo.allCount must be).equalTo(98)

      (page.paginationInfo.pagesCount must be).equalTo(10)

      (page.databaseObjects.size must be).equalTo(10)
    }

    "support aggregation filter and group" in {
      // #agg_execute
      val pipeline = List(filterStage, groupStage, sortStage)

      val pagination = MongoPaginatedAggregation(PersonDAO.Raw, pipeline, allowDiskUse = true)

      val page = pagination.paginate(1, 10)

      // #agg_execute
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

  }

}
