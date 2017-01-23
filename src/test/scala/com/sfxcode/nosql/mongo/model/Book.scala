package com.sfxcode.nosql.mongo.model

import java.util.Date

import org.bson.types.ObjectId

case class Author(var name: String)

case class Book(id: Option[Int], title: String, pages: BigInt, author: Author, set: Set[Long] = Set(1, 2, 3), released: Boolean = true, releaseDate: Date = new Date(), _id: ObjectId = new ObjectId())

object Book {

  val scalaBook = Book(Some(1), "Programming In Scala", 852, Author("Martin Odersky"), Set(2, 4, 10))

}