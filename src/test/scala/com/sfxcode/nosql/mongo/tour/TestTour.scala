package com.sfxcode.nosql.mongo.tour

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model._
import com.sfxcode.nosql.mongo.tour.Database._
import org.mongodb.scala.Completed

object TestTour extends App {

  // get a handle to the "test" collection
  val collection = BookDAO.collection

  BookDAO.drop().headResult()

  val scalaBook = Book(Some(1), "Programming In Scala", 852, Author("Martin Odersky"), List(2, 4, 10))

  val completed: Completed = BookDAO.insertOne(scalaBook)

  assert(BookDAO.count().headResult() == 1)

  BookDAO.deleteOne(scalaBook).headResult()

  assert(BookDAO.count().headResult() == 0)

  BookDAO.insert(scalaBook)

  val books: List[Book] = BookDAO.find().resultList()

  val book: Book = BookDAO.find().headResult()

  val book2 = BookDAO.findById(book._id).headResult()

  println(scalaBook)

  println(book)

  println(book._id)

  println(book.equals(scalaBook))

}

