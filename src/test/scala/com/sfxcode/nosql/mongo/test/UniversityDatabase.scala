package com.sfxcode.nosql.mongo.test

import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.model._
import com.sfxcode.nosql.mongo.server.LocalServer
import com.sfxcode.nosql.mongo.{GridFSDAO, MongoDAO}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._

object UniversityDatabase {
  // create local test server (mongo-java-server)
  var LocalTestServer: LocalServer = _

  // create codecs for custom classes
  private val universityRegistry: CodecRegistry = fromProviders(classOf[Student], classOf[Score], classOf[Grade])

  private val registry: CodecRegistry = fromRegistries(universityRegistry)

  // create provider
  val provider: DatabaseProvider = DatabaseProvider.fromPath(configPath = "unit.test.mongo.local", registry = registry)

  // setup DAO objects with mongo collection names

  object StudentDAO extends MongoDAO[Student](provider, "university-students")

  object GradeDAO extends MongoDAO[Book](provider, "university-grades")

  object SudentImagesDAO extends GridFSDAO(provider, "university-images")

}
