package com.sfxcode.nosql.mongo.json

import java.util.Date

import org.mongodb.scala.bson.ObjectId

case class Book(
    _id: Double,
    title: String,
    isbn: String,
    pageCount: Double,
    publishedDate: Date,
    thumbnailUrl: String,
    shortDescription: String,
    longDescription: String,
    status: String,
    authors: List[String],
    categories: List[String]
)

case class Restaurant(
    URL: String,
    _id: ObjectId,
    address: String,
    name: String,
    outcode: String,
    postcode: String,
    rating: Double,
    type_of_food: String
)

case class Person(id: Long,
                  guid: String,
                  isActive: Boolean,
                  balance: Double,
                  picture: String,
                  age: Int,
                  name: String,
                  gender: String,
                  email: String,
                  phone: String,
                  address: String,
                  about: String,
                  registered: Date,
                  tags: List[String],
                  friends: List[Friend],
                  greeting: String,
                  favoriteFruit: String,
                  _id: ObjectId = new ObjectId())

case class Friend(id: Long, name: String)

case class Product(
    _id: String,
    name: String,
    brand: String,
    `type`: String,
    price: Double,
    rating: Double,
    warranty_years: Double,
    available: Boolean
)

case class Student(
    _id: Long,
    name: String,
    scores: List[Score]
)

case class Score(
    score: Double,
    `type`: String
)

case class Grade(
    _id: ObjectId,
    student_id: Long,
    class_id: Long,
    scores: List[Score]
)
