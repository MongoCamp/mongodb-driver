package com.sfxcode.nosql.mongo.demo.tour

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model._
import org.mongodb.scala.result.InsertOneResult

object TestTour extends App {

  // get a handle to the "test" collection
  val collection = BookDAO.collection

  BookDAO.drop().result()

  val scalaBook = Book(Some(1), "Programming In Scala", 852, Author("Martin Odersky"), List(2, 4, 10))

  val completed: InsertOneResult = BookDAO.insertOne(scalaBook)

  assert(BookDAO.count().result() == 1)

  BookDAO.deleteOne(scalaBook).result()

  assert(BookDAO.count().result() == 0)

  BookDAO.insertOne(scalaBook).result()

  val books: List[Book] = BookDAO.find().resultList()

  val book: Book = BookDAO.find().result()

  val book2 = BookDAO.findById(book._id).result()

  println(scalaBook)

  println(book)

  println(book._id)

  println(book.equals(scalaBook))

  val bookJson = provider.dao("books").find(projection = Map("title" -> 1)).result().toJson()

  println(bookJson)

}
