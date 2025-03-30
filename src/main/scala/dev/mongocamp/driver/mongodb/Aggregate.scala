package dev.mongocamp.driver.mongodb

import dev.mongocamp.driver.mongodb.Aggregate.ZeroDividendStrategy
import dev.mongocamp.driver.mongodb.Aggregate.ZeroDividendStrategy.ZeroDividendStrategy
import org.bson.conversions.Bson
import org.mongodb.scala.model.Projections.computed

object Aggregate extends Aggregate {
  object ZeroDividendStrategy extends Enumeration {
    type ZeroDividendStrategy = Value
    val None, OutcomeZero, OutcomeOne, OutcomeDivisor = Value
  }
}

trait Aggregate extends Field with Filter with Sort {

  def compositeProjection(resultFieldName: String, keys: List[String]): Bson =
    computed(
      resultFieldName,
      Map[String, Any](
        "$concat" -> keys.map(
          key => Map[String, Any]("$substr" -> List("$" + key, 0, 99999))
        )
      )
    )

  def divideProjection(
    resultFieldName: String,
    dividendFieldName: String,
    divisorFieldName: String,
    zeroDividendStrategy: ZeroDividendStrategy = ZeroDividendStrategy.None
  ): Bson =
    if (ZeroDividendStrategy.None == zeroDividendStrategy)
      computed(resultFieldName, Map[String, Any]("$divide" -> List("$" + dividendFieldName, "$" + divisorFieldName)))
    else {
      val outcome: Any = {
        if (ZeroDividendStrategy.OutcomeDivisor == zeroDividendStrategy)
          "$" + dividendFieldName
        else if (ZeroDividendStrategy.OutcomeZero == zeroDividendStrategy)
          0
        else if (ZeroDividendStrategy.OutcomeOne == zeroDividendStrategy)
          1
        else
          0
      }
      computed(
        resultFieldName,
        Map[String, Any](
          "$cond" -> List(Map("$eq" -> List("$" + divisorFieldName, 0)), outcome, Map("$divide" -> List("$" + dividendFieldName, "$" + divisorFieldName)))
        )
      )
    }

  def multiplyProjection(resultFieldName: String, productFieldNames: List[String]): Bson =
    computed(
      resultFieldName,
      Map[String, Any](
        "$multiply" -> productFieldNames.map(
          fieldname => "$" + fieldname
        )
      )
    )

  def geoNearStage(
    lat: Double,
    long: Double,
    minDistanceInMeters: Option[Double] = None,
    maxDistanceInMeters: Option[Double] = None,
    distanceFieldName: String = "distance",
    spherical: Boolean = true
  ): Bson = {
    var additionalParameters: Map[String, Any] = Map.empty
    if (minDistanceInMeters.isDefined)
      additionalParameters ++= Map("minDistance" -> minDistanceInMeters.get)
    if (maxDistanceInMeters.isDefined)
      additionalParameters ++= Map("maxDistance" -> maxDistanceInMeters.get)
    Map[String, Any](
      "$geoNear" -> (Map(
        "near"          -> Map("type" -> "Point", "coordinates" -> List(long, lat)),
        "distanceField" -> distanceFieldName,
        "spherical"     -> spherical
      ) ++ additionalParameters)
    )
  }

}
