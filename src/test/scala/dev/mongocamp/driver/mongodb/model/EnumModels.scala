package dev.mongocamp.driver.mongodb.model

import org.mongodb.scala.bson.ObjectId

object Season extends Enumeration {
  val Spring, Summer, Autumn, Winter = Value
}

sealed abstract class Direction
object Direction {
  case object North extends Direction
  case object South extends Direction
  case object East  extends Direction
  case object West  extends Direction

  def valueOf(name: String): Direction = name match {
    case "North" => North
    case "South" => South
    case "East"  => East
    case "West"  => West
    case other   => throw new IllegalArgumentException(s"Unknown Direction: $other")
  }
}

object Status extends Enumeration {
  type Status = Status.Value

  val Valid   = Value("VALID")
  val Invalid = Value("INVALID")

}

case class EnumDocument(javaColor: Color, season: Season.Value, direction: Direction, status: Status.Value, _id: ObjectId = new ObjectId())
