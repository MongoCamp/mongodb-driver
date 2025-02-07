package dev.mongocamp.driver.mongodb.pagination

import dev.mongocamp.driver.mongodb.database.ConfigHelper

trait MongoPagination[A <: Any] extends ConfigHelper {
  def paginate(page: Long, rows: Long): PaginationResult[A]
  def countResult: Long

  def foreach(a: A => Unit): Unit = {
    val rows = intConfig(configPath = "dev.mongocamp.mongodb.pagination", key = "rows")
    foreach(rows)(a)
  }

  def foreach(rows: Int)(a: A => Unit): Unit = {
    var currentPageNumber = 1
    val rowsPerPage       = if (rows < 1) Int.MaxValue else rows
    val maxPages          = Math.ceil(countResult.toDouble / rowsPerPage).toInt
    while (currentPageNumber > 0 && rowsPerPage > 0 && currentPageNumber <= maxPages) {
      val page = paginate(currentPageNumber, rowsPerPage)
      page.databaseObjects.foreach(a)
      currentPageNumber += 1
    }
  }

}
