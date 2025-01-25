package dev.mongocamp.driver.mongodb.pagination

import dev.mongocamp.driver.mongodb.exception.MongoCampPaginationException
import dev.mongocamp.driver.mongodb.{ MongoDAO, _ }
import io.circe.Decoder
import io.circe.generic.auto._
import org.mongodb.scala.bson.conversions.Bson

case class MongoPaginatedFilter[A <: Any](dao: MongoDAO[A], filter: Bson = Map(), sort: Bson = Map(), projection: Bson = Map(), maxWait: Int = DefaultMaxWait)(
    implicit decoder: Decoder[A]
) extends MongoPagination[A] {

  def paginate(page: Long, rows: Long): PaginationResult[A] = {
    val count = countResult
    if (rows <= 0) {
      throw MongoCampPaginationException("rows per page must be greater then 0.")
    }
    if (page <= 0) {
      throw MongoCampPaginationException("page must be greater then 0.")
    }
    val allPages = Math.ceil(count.toDouble / rows).toInt
    val skip     = (page - 1) * rows
    // todo: add projection
    val responseList = dao.find(filter, sort, projection, rows.toInt).resultList(maxWait)
//    val responseList = dao.find(filter, sort, projection, rows.toInt).skip(skip.toInt).resultList(maxWait)
    PaginationResult(responseList, PaginationInfo(count, rows, page, allPages))
  }

  def countResult: Long = dao.count(filter).result(maxWait)

}
