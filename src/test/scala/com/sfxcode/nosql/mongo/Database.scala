package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.json4s.DefaultBsonSerializer._
import org.mongodb.scala._

import com.sfxcode.nosql.mongo.model._

object Database {

  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("simple_mongo_test")

  val bookCollection: MongoCollection[Document] = database.getCollection("books")

  object BookDAO extends MongoDAO[Book](Database.bookCollection)

  val lineCollection: MongoCollection[Document] = database.getCollection("lines")

  object LineDAO extends MongoDAO[Line](Database.lineCollection)

  val personCollection: MongoCollection[Document] = database.getCollection("person")

  object PersonDAO extends MongoDAO[Person](Database.personCollection)

  PersonDAO.dropResult()

  val completed: Completed = PersonDAO.insertResult(Person.personList)

  def printDatabaseStatus(): Unit = {
    printDebugValues("Database Status", "%s rows for collection person found".format(PersonDAO.count()))
  }

  def printDebugValues(name: String, result: Any): Unit = {
    println()
    println(name)
    println(result)
  }

}
