package dev.mongocamp.driver.mongodb.json

import dev.mongocamp.driver.mongodb.json.model.Status
import io.circe.Json

class EncodeAnyToJsonSuite extends munit.FunSuite {

  test("encodeAnyToJson converts Enumeration values to strings") {
    assertEquals(encodeAnyToJson(Status.ACTIVE), Json.fromString("ACTIVE"))
    assertEquals(encodeAnyToJson(Some(Status.ACTIVE)), Json.fromString("ACTIVE"))
    assertEquals(encodeAnyToJson(List(Status.ACTIVE, Status.INACTIVE)), Json.arr(Json.fromString("ACTIVE"), Json.fromString("INACTIVE")))
    assertEquals(encodeAnyToJson(Map("s" -> Status.ACTIVE)), Json.obj("s" -> Json.fromString("ACTIVE")))
  }

  test("encodeAnyToJson converts unknown types with toString") {
    val unknown = new Object {
      override def toString: String = "unknown value"
    }
    assertEquals(encodeAnyToJson(unknown), Json.fromString("unknown value"))
    assertEquals(encodeAnyToJson(Map("value" -> unknown)), Json.obj("value" -> Json.fromString("unknown value")))
  }

  test("encodeAnyToJson converts other numbers to numbers") {
    assertEquals(encodeAnyToJson(5.toShort), Json.fromInt(5))
    assertEquals(encodeAnyToJson(5.toByte), Json.fromInt(5))
    assertEquals(encodeAnyToJson(new java.math.BigDecimal("1.5")), Json.fromBigDecimal(BigDecimal("1.5")))
    assertEquals(encodeAnyToJson(java.math.BigInteger.TEN), Json.fromBigInt(BigInt(10)))
  }

  test("encodeAnyToJson keeps null values") {
    assertEquals(encodeAnyToJson(null), Json.Null)
    assertEquals(encodeAnyToJson(Map("value" -> null)), Json.obj("value" -> Json.Null))
  }

  test("CirceSchema converts Enumeration values to strings") {
    @annotation.nowarn("cat=deprecation")
    val schema = new CirceSchema {}
    assertEquals(schema.encodeAnyToJson(Status.ACTIVE), Json.fromString("ACTIVE"))
  }

  test("decodeFromJson returns Enumeration values as string") {
    assertEquals(decodeFromJson(encodeAnyToJson(Status.ACTIVE)), "ACTIVE")
  }

}
