package com.sfxcode.nosql.mongo.tour

import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.model._
import org.mongodb.scala._

object Database extends ObservableImplicits {

  val mongoClient: MongoClient = MongoClient()

  import org.bson.codecs.configuration.CodecRegistries._
  import org.mongodb.scala.bson.codecs.Macros._

  private val bookRegistry = fromProviders(classOf[Book], classOf[Author])

  private val personRegistry = fromProviders(classOf[Person], classOf[Friend])

  private val lineRegistry = fromProviders(classOf[Line], classOf[Position])

  val database = DatabaseProvider("simple_mongo_test", fromRegistries(bookRegistry, personRegistry, lineRegistry))

  object BookDAO extends MongoDAO[Book](database, "books")

  object LineDAO extends MongoDAO[Line](database, "lines")

  object PersonDAO extends MongoDAO[Person](database, "person")

  PersonDAO.dropResult()

  val persons: List[Person] = Person.personList

  PersonDAO.insertValuesResult(persons)

  def printDatabaseStatus(): Unit = {
    printDebugValues("Database Status", "%s rows for collection person found".format(PersonDAO.count()))
  }

  def printDebugValues(name: String, result: Any): Unit = {
    println()
    println(name)
    println(result)
  }

}
