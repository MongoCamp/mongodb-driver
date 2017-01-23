package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.Database._
import com.sfxcode.nosql.mongo.model._

/**
 * Created by tom on 20.01.17.
 */
object QuickTour extends App {

  println(LineDAO.dropResult())

  val line = Line(1, "default", 3, Position(1, 3), Position(3, 7))

  LineDAO.insert(line)

  printDebugValues("LineDAO.findAll", LineDAO.findAll())

  val lines = (1 to 100) map { i: Int => Line(i * 10, "default", 1000 + i, Position(1, 3), Position(3, 7)) }

  LineDAO.insertResult(lines)

  printDebugValues("LineDAO.count", LineDAO.count())

  printDebugValues("LineDAO.findOneByName", LineDAO.findOne("id", 710))

  println(LineDAO.distinct("index"))

}
