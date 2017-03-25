package com.sfxcode.nosql.mongo.tour

import com.sfxcode.nosql.mongo.model._
import com.sfxcode.nosql.mongo.tour.Database._

object TestTour extends App {

  // get a handle to the "test" collection
  val collection = Database.bookCollection

  BookDAO.dropResult()

  val scalaBook = Book(Some(1), "Programming In Scala", 852, Author("Martin Odersky"), Set(2, 4, 10))

  BookDAO.insertResult(scalaBook)

  assert(BookDAO.countResult() == 1)

  BookDAO.deleteResult(scalaBook)

  assert(BookDAO.countResult() == 0)

  BookDAO.insertResult(scalaBook)

  val books: List[Book] = BookDAO.findAll()

  val book: Book = BookDAO.findAll().head

  val book2 = BookDAO.findOneById(book._id)

  println(scalaBook)
  println(book)

  println(book._id)

  println(book.equals(scalaBook))

}

