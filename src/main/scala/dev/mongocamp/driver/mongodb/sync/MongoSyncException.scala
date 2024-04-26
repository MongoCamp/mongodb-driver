package dev.mongocamp.driver.mongodb.sync

case class MongoSyncException(message: String) extends Exception(message)
