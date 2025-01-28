package dev.mongocamp.driver.mongodb.test

import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.model._
import dev.mongocamp.driver.mongodb.server.LocalServer
import dev.mongocamp.driver.mongodb.{GridFSDAO, MongoDAO}

import dev.mongocamp.driver.mongodb.json._
import io.circe.syntax._
import io.circe.generic.auto._


object UniversityDatabase {
  // create local test server (mongodb-java-server)
  var LocalTestServer: LocalServer = _

  // create codecs for custom classes

  // create provider
  val provider: DatabaseProvider = DatabaseProvider.fromPath(configPath = "unit.test.mongo.local")

  // setup DAO objects with mongodb collection names

  object StudentDAO extends MongoDAO[Student](provider, "university-students")

  object GradeDAO extends MongoDAO[Book](provider, "university-grades")

  object SudentImagesDAO extends GridFSDAO(provider, "university-images")

}
