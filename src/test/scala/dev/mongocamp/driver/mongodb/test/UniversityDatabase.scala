package dev.mongocamp.driver.mongodb.test

import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.model._
import dev.mongocamp.driver.mongodb.server.LocalServer
import dev.mongocamp.driver.mongodb.GridFSDAO
import dev.mongocamp.driver.mongodb.MongoDAO

object UniversityDatabase {
  // create local test server (mongodb-java-server)
  var LocalTestServer: LocalServer = _

  // create codecs for custom classes

  // create provider
  val provider: DatabaseProvider = DatabaseProvider.fromPath(configPath = "unit.test.mongo.local")

  // setup DAO objects with mongodb collection names

  object StudentDAO extends MongoDAO[Student](provider, "universityStudents")

  object GradeDAO extends MongoDAO[Grade](TestDatabase.provider, "universityGrades")

  object SudentImagesDAO extends GridFSDAO(provider, "universityImages")

}
