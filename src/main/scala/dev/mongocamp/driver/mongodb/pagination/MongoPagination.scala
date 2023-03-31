package dev.mongocamp.driver.mongodb.pagination

trait MongoPagination[A <: Any] {
  def paginate(page: Long, rows: Long): PaginationResult[A]
  def countResult: Long
}
object MongoPagination {
  def foreach[A <: Any](pagination: MongoPagination[A], rows: Int = 50)(a: A => Unit): Unit = {
    var currentPageNumber = 1
    val rowsPerPage = if (rows < 1) Int.MaxValue else rows
    val maxPages    = Math.ceil(pagination.countResult.toDouble / rowsPerPage).toInt
    while (currentPageNumber > 0 && rowsPerPage > 0 && currentPageNumber <= maxPages) {
      val page = pagination.paginate(currentPageNumber, rowsPerPage)
      page.databaseObjects.foreach(a)
      currentPageNumber += 1
    }

  }

}
