package com.sfxcode.nosql.mongo.model

import java.util.Date

import org.bson.types.ObjectId

case class Author(var name: String)

case class Book(id: Option[Int],
                title: String,
                pages: BigInt,
                author: Author,
                indexes: List[Long] = List(1, 2, 3),
                released: Boolean = true,
                releaseDate: Date = new Date(),
                _id: ObjectId = new ObjectId())

object Book {

  def scalaBook(id: Int = 1) = Book(Some(id), "Programming In Scala", 852, Author("Martin Odersky"), List(2, 4, 10))

}
