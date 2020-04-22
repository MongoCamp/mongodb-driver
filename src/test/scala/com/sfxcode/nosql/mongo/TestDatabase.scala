package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.model._
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.bson.codecs.Macros._

object TestDatabase {

  private val registry           = fromProviders(classOf[Person], classOf[Friend], classOf[CodecTest])
  private val universityRegistry = fromProviders(classOf[Student], classOf[Score], classOf[Grade])

  val provider =
    DatabaseProvider.fromPath(configPath = "unit.test.mongo", registry = fromRegistries(registry, universityRegistry))

  object PersonDAO extends MongoDAO[Person](provider, "people")

  object BookDAO extends MongoDAO[Book](provider, "books")

  object CodecDao extends MongoDAO[CodecTest](provider, "codec:codec-test")

  object StudentDAO extends MongoDAO[Book](provider, "university-students")

  object GradeDAO extends MongoDAO[Book](provider, "university-grades")

}
