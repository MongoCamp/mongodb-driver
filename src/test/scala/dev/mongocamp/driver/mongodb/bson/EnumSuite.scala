package dev.mongocamp.driver.mongodb.bson

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.model._
import dev.mongocamp.driver.mongodb.test.TestDatabase.provider
import dev.mongocamp.driver.mongodb.MongoDAO
import io.circe.Decoder
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.documentToUntypedDocument
import org.mongodb.scala.model.Filters.equal

class EnumSuite extends munit.FunSuite with LazyLogging {

  implicit val colorDecoder: Decoder[Color] = Decoder[String].emap(
    s => scala.util.Try(Color.valueOf(s)).toEither.left.map(_.getMessage)
  )

  implicit val seasonDecoder: Decoder[Season.Value] = Decoder[String].emap(
    s => scala.util.Try(Season.withName(s)).toEither.left.map(_.getMessage)
  )

  implicit val statusDecoder: Decoder[Status.Value] = Decoder[String].emap(
    s => scala.util.Try(Status.withName(s)).toEither.left.map(_.getMessage)
  )

  implicit val directionDecoder: Decoder[Direction] = Decoder[String].emap(
    s => scala.util.Try(Direction.valueOf(s)).toEither.left.map(_.getMessage)
  )

  object EnumDAO extends MongoDAO[EnumDocument](provider, "enum_test")

  override def beforeAll(): Unit = EnumDAO.drop().result()
  override def afterAll(): Unit  = EnumDAO.drop().result()

  private def roundTrip(doc: EnumDocument): EnumDocument = {
    EnumDAO.insertOne(doc).result()
    EnumDAO.find(equal("_id", doc._id)).result()
  }

  test("Java enum Color.RED survives a MongoDB round-trip") {
    val doc    = EnumDocument(Color.RED, Season.Spring, Direction.North, Status.Valid)
    val result = roundTrip(doc)
    assertEquals(result.javaColor, Color.RED)
  }

  test("Java enum Color.GREEN survives a MongoDB round-trip") {
    val doc    = EnumDocument(Color.GREEN, Season.Spring, Direction.North, Status.Invalid)
    val result = roundTrip(doc)
    assertEquals(result.javaColor, Color.GREEN)
  }

  test("Java enum Color.BLUE survives a MongoDB round-trip") {
    val doc    = EnumDocument(Color.BLUE, Season.Spring, Direction.North, Status.Valid)
    val result = roundTrip(doc)
    assertEquals(result.javaColor, Color.BLUE)
  }

  test("Java enum is stored as its name string in BSON") {
    val doc = EnumDocument(Color.RED, Season.Spring, Direction.North, Status.Valid, new ObjectId())
    EnumDAO.insertOne(doc).result()
    val raw = EnumDAO.Raw.find(equal("_id", doc._id)).result()
    assertEquals(raw.getString("javaColor"), "RED")
  }

  test("Scala Enumeration Season.Summer survives a MongoDB round-trip") {
    val doc    = EnumDocument(Color.GREEN, Season.Summer, Direction.South, Status.Valid)
    val result = roundTrip(doc)
    assertEquals(result.season, Season.Summer)
  }

  test("all Season values survive MongoDB round-trips") {
    Season.values.foreach {
      season =>
        val doc    = EnumDocument(Color.RED, season, Direction.North, Status.Valid, new ObjectId())
        val result = roundTrip(doc)
        assertEquals(result.season, season, s"Season.${season} should round-trip correctly")
    }
  }

  test("Scala Enumeration is stored as its name string in BSON") {
    val doc = EnumDocument(Color.RED, Season.Autumn, Direction.North, Status.Valid, new ObjectId())
    EnumDAO.insertOne(doc).result()
    val raw = EnumDAO.Raw.find(equal("_id", doc._id)).result()
    assertEquals(raw.getString("season"), "Autumn")
  }

  test("sealed-trait Direction.West survives a MongoDB round-trip") {
    val doc    = EnumDocument(Color.BLUE, Season.Winter, Direction.West, Status.Valid)
    val result = roundTrip(doc)
    assertEquals(result.direction, Direction.West)
  }

  test("all Direction values survive MongoDB round-trips") {
    Seq(Direction.North, Direction.South, Direction.East, Direction.West).foreach {
      dir =>
        val doc    = EnumDocument(Color.RED, Season.Spring, dir, Status.Valid, new ObjectId())
        val result = roundTrip(doc)
        assertEquals(result.direction, dir, s"Direction $dir should round-trip correctly")
    }
  }

  test("sealed-trait case object is stored as its name string in BSON") {
    val doc = EnumDocument(Color.RED, Season.Spring, Direction.East, Status.Valid, new ObjectId())
    EnumDAO.insertOne(doc).result()
    val raw = EnumDAO.Raw.find(equal("_id", doc._id)).result()
    assertEquals(raw.getString("direction"), "East")
  }

  test("all three enum types in one document survive a MongoDB round-trip") {
    val doc    = EnumDocument(Color.GREEN, Season.Winter, Direction.South, Status.Valid)
    val result = roundTrip(doc)

    assertEquals(result.javaColor, Color.GREEN)
    assertEquals(result.season, Season.Winter)
    assertEquals(result.direction, Direction.South)
  }

  test("insert and query multiple documents with different enum values") {
    val docs = Seq(
      EnumDocument(Color.RED, Season.Spring, Direction.North, Status.Valid, new ObjectId()),
      EnumDocument(Color.GREEN, Season.Summer, Direction.East, Status.Valid, new ObjectId()),
      EnumDocument(Color.BLUE, Season.Autumn, Direction.South, Status.Valid, new ObjectId()),
      EnumDocument(Color.RED, Season.Winter, Direction.West, Status.Valid, new ObjectId())
    )
    docs.foreach(
      d => EnumDAO.insertOne(d).result()
    )

    val reds = EnumDAO.find(equal("javaColor", "RED")).resultList()
    assert(reds.nonEmpty)
    assert(reds.forall(_.javaColor == Color.RED))

    val springs = EnumDAO.find(equal("season", "Spring")).resultList()
    assert(springs.nonEmpty)
    assert(springs.forall(_.season == Season.Spring))

    val norths = EnumDAO.find(equal("direction", "North")).resultList()
    assert(norths.nonEmpty)
    assert(norths.forall(_.direction == Direction.North))
  }

}
