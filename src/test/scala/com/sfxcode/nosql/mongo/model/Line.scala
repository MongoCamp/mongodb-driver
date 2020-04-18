package com.sfxcode.nosql.mongo.model

import org.mongodb.scala.bson.ObjectId

/**
 * Created by tom on 20.01.17.
 */
case class Line(
  var id: Long,
  var name: String,
  index: Int,
  bottomLeft: Position,
  bottomRight: Position,
  _id: ObjectId = new ObjectId())

object Line {
  def line(id: Long = 1) = Line(id, "default", 3, Position(1, 3), Position(3, 7))
  val lines = (1 to 100) map { i: Int =>
    Line(i * 10, "default", 1000 + i, Position(1, 3), Position(3, 7))
  }

}

case class Position(x: Int, y: Int)
