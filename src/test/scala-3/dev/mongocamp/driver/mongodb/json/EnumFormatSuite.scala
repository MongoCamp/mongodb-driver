package dev.mongocamp.driver.mongodb.json

import io.circe.Json

enum Color {
  case Red, Green
  case Rgb(red: Int, green: Int, blue: Int)
}

class EnumFormatSuite extends munit.FunSuite {

  test("encodeAnyToJson converts enum values without parameters to strings") {
    assertEquals(encodeAnyToJson(Color.Red), Json.fromString("Red"))
    assertEquals(encodeAnyToJson(List(Color.Red, Color.Green)), Json.arr(Json.fromString("Red"), Json.fromString("Green")))
  }

  test("encodeAnyToJson converts enum values with parameters to objects") {
    assertEquals(encodeAnyToJson(Color.Rgb(1, 2, 3)), Json.obj("red" -> Json.fromInt(1), "green" -> Json.fromInt(2), "blue" -> Json.fromInt(3)))
  }

}
