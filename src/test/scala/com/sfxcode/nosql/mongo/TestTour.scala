package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.Database._
import com.sfxcode.nosql.mongo.model._
/**
 * Created by tom on 20.01.17.
 */
object TestTour extends App {

  // get a handle to the "test" collection
  val collection = Database.bookCollection

  BookDAO.drop()

  val scalaBook = Book(Some(1), "Programming In Scala", 852, Author("Martin Odersky"), Set(2, 4, 10))

  BookDAO.insertResult(scalaBook)

  assert(BookDAO.count() == 1)

  BookDAO.deleteResult(scalaBook)

  assert(BookDAO.count() == 0)

  BookDAO.insertResult(scalaBook)

  println(collection.find.headResult().toJson())

  val books: List[Book] = BookDAO.findAll()

  val book: Book = BookDAO.findAll().head

  val book2 = BookDAO.findOneById(book._id)

  println(scalaBook)
  println(book)

  println(book._id)

  println(book.equals(scalaBook))

}

