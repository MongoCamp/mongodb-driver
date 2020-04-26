package com.sfxcode.nosql.mongo.operation

// #agg_imports
import com.sfxcode.nosql.mongo.Aggregate._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.dao.PersonSpecification
// #agg_imports

import com.sfxcode.nosql.mongo.test.TestDatabase._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{filter, group, sort}
import org.mongodb.scala.model.Filters.{and, equal}

class AggregationSpec extends PersonSpecification {

  // #agg_stages
  val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

  val groupStage: Bson = group(Map("age" -> "$age"), sumField("balance"), firstField("age"))

  val sortStage: Bson = sort(sortByKey("age"))
  // #agg_stages

  "Search" should {

    "support aggregation filter" in {

      val aggregator = List(filterStage, sortStage)

      val aggregated = PersonDAO.findAggregated(aggregator).resultList()

      aggregated.size must be equalTo 98

    }

    "support aggregation filter and group" in {
      // #agg_execute
      val aggregator = List(filterStage, groupStage, sortStage)

      val aggregated = PersonDAO.Raw.findAggregated(aggregator).resultList()

      // #agg_execute

      aggregated.size must be equalTo 21

      // #agg_convert
      val list: List[Map[String, Any]] = aggregated
      // #agg_convert
      list.foreach(m => println(m("age") + " -> " + m("balance")))

      list.head("age") must be equalTo 20
      list.head("balance") must be equalTo 8333.0

    }

  }

}
