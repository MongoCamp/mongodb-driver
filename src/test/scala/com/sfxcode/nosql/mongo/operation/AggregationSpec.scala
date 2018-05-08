package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.Sort._
import com.sfxcode.nosql.mongo.Field._

import com.sfxcode.nosql.mongo.TestDatabase._

import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{ filter, group, sort }
import org.mongodb.scala.model.Filters.equal

import org.specs2.mutable.{ Before, Specification }

class AggregationSpec extends Specification with Before {

  sequential

  val groupStage: Bson = group(Map("age" -> "$age"), sumField("balance"), firstField("age"))

  val filterStage: Bson = filter(equal("gender", "female"))

  val sortStage: Bson = sort(sortByKey("age"))

  "Search" should {

    "support aggregation filter" in {

      val aggregator = List(filterStage, sortStage)

      val aggregated = PersonDAO.findAggregated(aggregator).resultList()

      aggregated.size must be equalTo 98

    }

    "support aggregation filter and group" in {

      val aggregator = List(filterStage, groupStage, sortStage)

      val aggregated = PersonDAO.Raw.findAggregated(aggregator).resultList()

      aggregated.size must be equalTo 21

      val list: List[Map[String, Any]] = aggregated

      list.foreach(m => println(m("age") + " -> " + m("balance")))

      list.head("age") must be equalTo 20
      list.head("balance") must be equalTo 8333.0

    }

  }

  override def before: Any = printDatabaseStatus()
}
