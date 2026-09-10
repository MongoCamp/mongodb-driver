package dev.mongocamp.driver.mongodb.pagination

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.exception.MongoCampPaginationException
import java.util.concurrent.TimeUnit
import org.mongodb.scala.bson.conversions.Bson
import scala.concurrent.duration.Duration

object MongoPaginatedFilter {
  def apply[A <: Any](dao: MongoDAO[A], filter: Bson, sort: Bson, projection: Bson, maxWait: Int): MongoPaginatedFilter[A] =
    MongoPaginatedFilter(dao, filter, sort, projection, Duration(maxWait, TimeUnit.SECONDS))
}

case class MongoPaginatedFilter[A <: Any](
  dao: MongoDAO[A],
  filter: Bson = Map(),
  sort: Bson = Map(),
  projection: Bson = Map(),
  maxWait: Duration = DefaultMaxWaitDuration
) extends MongoPagination[A] {

  def paginate(page: Int, rows: Int): PaginationResult[A] = {
    val count = countResult
    if (rows <= 0) {
      throw MongoCampPaginationException("rows per page must be greater then 0.")
    }
    if (page <= 0) {
      throw MongoCampPaginationException("page must be greater then 0.")
    }
    val allPages     = Math.ceil(count.toDouble / rows).toInt
    val skip         = (page - 1) * rows
    val responseList = dao.find(filter, sort, projection, rows, skip).resultList(maxWait)
    PaginationResult(responseList, PaginationInfo(count, rows, page, allPages))
  }

  def countResult: Long = dao.count(filter).result(maxWait)

}
