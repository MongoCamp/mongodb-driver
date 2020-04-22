package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.model._
import org.mongodb.scala._

object TestDatabase extends ObservableImplicits {

  val mongoClient: MongoClient = MongoClient()

  import org.bson.codecs.configuration.CodecRegistries._
  import org.mongodb.scala.bson.codecs.Macros._

  private val registry = fromProviders(classOf[Person], classOf[Friend], classOf[CodecTest])
  private val universityRegistry = fromProviders(classOf[Student], classOf[Score], classOf[Grade])

  val provider =
    DatabaseProvider.fromPath(configPath = "unit.test.mongo", registry = fromRegistries(registry, universityRegistry))

  object PersonDAO extends MongoDAO[Person](provider, "people")

  object BookDAO extends MongoDAO[Book](provider, "books")

  object CodecDao extends MongoDAO[CodecTest](provider, "codec:codec-test")

  object StudentDAO extends MongoDAO[Book](provider, "university-students")
  object GradeDAO extends MongoDAO[Book](provider, "university-grades")

  def printDatabaseStatus(): Unit = {
    val count: Long = PersonDAO.count()
    printDebugValues("Database Status", "%s rows for collection person found".format(count))
  }

  def printDebugValues(name: String, result: Any): Unit = {
    println()
    println(name)
    println(result)
  }

}
