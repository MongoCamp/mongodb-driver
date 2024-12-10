package dev.mongocamp.driver.mongodb.test

import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.model._
import dev.mongocamp.driver.mongodb.server.LocalServer
import dev.mongocamp.driver.mongodb.{GridFSDAO, MongoDAO}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._

object UniversityDatabase {
  // create local test server (mongodb-java-server)
  var LocalTestServer: LocalServer = _

  // create codecs for custom classes
  private val universityRegistry: CodecRegistry = fromProviders(classOf[Student], classOf[Score], classOf[Grade])

  private val registry: CodecRegistry = fromRegistries(universityRegistry)

  // create provider
  val provider: DatabaseProvider = DatabaseProvider.fromPath(configPath = "unit.test.mongo.local", registry = registry)

  // setup DAO objects with mongodb collection names

  object StudentDAO extends MongoDAO[Student](provider, "university-students")

  object GradeDAO extends MongoDAO[Book](provider, "university-grades")

  object SudentImagesDAO extends GridFSDAO(provider, "university-images")

}
