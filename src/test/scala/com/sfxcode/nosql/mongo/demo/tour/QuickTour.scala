package com.sfxcode.nosql.mongo.demo.tour

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model._
import TestDatabase._
import org.mongodb.scala.Void
import org.mongodb.scala.result.{InsertManyResult, InsertOneResult}

object QuickTour extends App {

  println(LineDAO.drop())

  val line = Line(1, "default", 3, Position(1, 3), Position(3, 7))

  val insertOneResult: InsertOneResult = LineDAO.insertOne(line)

  printDebugValues("LineDAO.findAll", LineDAO.find())

  val lines = (1 to 100) map { i: Int => Line(i * 10, "default", 1000 + i, Position(1, 3), Position(3, 7)) }

  val insertManyResult: InsertManyResult = LineDAO.insertMany(lines)

  printDebugValues("LineDAO.count", LineDAO.count())

  printDebugValues("LineDAO.findOneByName", LineDAO.find("id", 710))

  println(LineDAO.distinct("index").resultList())

}
