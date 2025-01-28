package dev.mongocamp.driver.mongodb.operation

// #region agg_imports
import dev.mongocamp.driver.mongodb.Aggregate._
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
// #endregion agg_imports

import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{ filter, group, sort }
import org.mongodb.scala.model.Filters.{ and, equal }

class AggregationSpec extends PersonSpecification {

  // #region agg_stages
  val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

  val groupStage: Bson = group(Map("age" -> "$age"), sumField("balance"), firstField("age"))

  val sortStage: Bson = sort(sortByKey("age"))
  // #endregion agg_stages

  "Search" should {

    "support aggregation filter" in {

      val pipeline = List(filterStage, sortStage)

      val aggregated = PersonDAO.findAggregated(pipeline).resultList()

      (aggregated.size must be).equalTo(98)

    }

    "support aggregation filter and group" in {
      // #region agg_execute
      val pipeline = List(filterStage, groupStage, sortStage)

      val aggregated = PersonDAO.Raw.findAggregated(pipeline).resultList()
      // #endregion agg_execute

      (aggregated.size must be).equalTo(21)

      // #region agg_convert
      val list: List[Map[String, Any]] = aggregated
      // #endregion agg_convert
      list.foreach(m => println(m("age").toString + " -> " + m("balance")))

      (list.head("age") must be).equalTo(20)
      (list.head("balance") must be).equalTo(8333.0)

    }

  }

}
