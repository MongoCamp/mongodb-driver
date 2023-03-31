package dev.mongocamp.driver.mongodb.pagination

case class PaginationInfo(allCount: Long, perPage: Long, page: Long, pagesCount: Long)
