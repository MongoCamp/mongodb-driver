package com.sfxcode.nosql.mongo.model

import java.text.SimpleDateFormat
import java.util.Date

import org.json4s.DefaultFormats
import org.json4s.native.Serialization._
import org.mongodb.scala.bson.ObjectId

import scala.io.Source

case class Person(
  id: Long,
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

object Person {

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
  }

  val personList: List[Person] = read[List[Person]](fromJson("/test_data.json"))

  def fromJson(name: String): String = {
    val is = getClass.getResourceAsStream(name)
    Source.fromInputStream(is, "UTF-8").getLines().mkString
  }

}
