package dev.mongocamp.driver.mongodb.pagination

case class PaginationResult[A <: Any](databaseObjects: List[A], paginationInfo: PaginationInfo)
