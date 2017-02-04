# simple-mongo

A small library for case class support with the mongo-scala-driver.

## Version


Scala Version is 2.12.1 / 2.11.8.

## Travis

[![Build Status](https://travis-ci.org/sfxcode/simple-mongo.svg?branch=master)](https://travis-ci.org/sfxcode/simple-mongo)

## Download

[ ![Download](https://api.bintray.com/packages/sfxcode/maven/simple-mongo/images/download.svg) ](https://bintray.com/sfxcode/maven/simple-mongo/_latestVersion)

## Licence

Apache 2 License.

## Usage

Define MongoDB Connection and DAO objects for automatic case class conversion.


```scala
import com.sfxcode.nosql.mongo.json4s.DefaultBsonSerializer._
import org.mongodb.scala._

import com.sfxcode.nosql.mongo.model._

object Database {

  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("simple_mongo_test")

  val bookCollection: MongoCollection[Document] = database.getCollection("books")

  object BookDAO extends MongoDAO[Book](Database.bookCollection)
}
```


Import the database object and execute find and CRUD functions on the DAO object.

```scala

import Database._

   case class Book(id: Option[Int], title: String, pages: BigInt, author: Author, 
    set: Set[Long] = Set(1, 2, 3), released: Boolean = true, releaseDate: Date = new Date(),
    _id: ObjectId = new ObjectId())


  val books: List[Book] = BookDAO.findAll()


  val scalaBook = Book(Some(1), "Programming In Scala", 852, Author("Martin Odersky"))
  
    BookDAO.insertResult(scalaBook)
    
    BookDAO.deleteResult(scalaBook)
  
```





