package dev.mongocamp.driver.mongodb.pagination

import com.mongodb.client.model.Facet
import dev.mongocamp.driver.mongodb.exception.MongoCampPaginationException
import dev.mongocamp.driver.mongodb.*
import org.mongodb.scala.bson.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates

import scala.jdk.CollectionConverters._
import dev.mongocamp.driver.mongodb.json._

case class MongoPaginatedAggregation[A <: Any](
    dao: MongoDAO[A],
    aggregationPipeline: List[Bson] = List(),
    allowDiskUse: Boolean = false,
    maxWait: Int = DefaultMaxWait
) extends MongoPagination[Document] {

  private val AggregationKeyMetaData      = "metadata"
  private val AggregationKeyData          = "data"
  private val AggregationKeyMetaDataTotal = "total"

  def paginate(page: Long, rows: Long): PaginationResult[Document] = {
    if (rows <= 0) {
      throw MongoCampPaginationException("rows per page must be greater then 0.")
    }
    if (page <= 0) {
      throw MongoCampPaginationException("page must be greater then 0.")
    }

    val skip = (page - 1) * rows

    val listOfMetaData: List[Bson] = List(Map("$count" -> AggregationKeyMetaDataTotal))
    val listOfPaging: List[Bson]   = List(Map("$skip" -> skip), Map("$limit" -> rows))

    val pipeline =
      aggregationPipeline ++ List(
        Aggregates.facet(new Facet(AggregationKeyMetaData, listOfMetaData.asJava), new Facet(AggregationKeyData, listOfPaging.asJava))
      )

    val dbResponse = dao.Raw.findAggregated(pipeline, allowDiskUse).result(maxWait)

    val count: Long = dbResponse
      .get(AggregationKeyMetaData)
      .get
      .asArray()
      .asScala
      .headOption
      .map(v => v.asDocument().get(AggregationKeyMetaDataTotal).asNumber().longValue())
      .getOrElse(0)

    val allPages = Math.ceil(count.toDouble / rows).toInt
    val list     = dbResponse.get("data").get.asArray().asScala.map(_.asDocument()).map(bdoc => Document(bdoc))
    PaginationResult(list.toList, PaginationInfo(count, rows, page, allPages))
  }

  def countResult: Long = {
    val listOfMetaData: List[Bson] = List(Map("$count" -> AggregationKeyMetaDataTotal))
    val listOfPaging: List[Bson]   = List(Map("$skip" -> 0), Map("$limit" -> 1))

    val pipeline = aggregationPipeline ++ List(
      Aggregates.facet(new Facet(AggregationKeyMetaData, listOfMetaData.asJava), new Facet(AggregationKeyData, listOfPaging.asJava))
    )
    val dbResponse = dao.Raw.findAggregated(pipeline, allowDiskUse).result(maxWait)
    val count: Long = dbResponse
      .get(AggregationKeyMetaData)
      .get
      .asArray()
      .asScala
      .headOption
      .map(v => v.asDocument().get(AggregationKeyMetaDataTotal).asNumber().longValue())
      .getOrElse(0)

    count
  }

}
