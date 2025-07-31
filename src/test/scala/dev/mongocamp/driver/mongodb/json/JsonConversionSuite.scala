package dev.mongocamp.driver.mongodb.json

case class HelloWorld(greetings: String, name: String)
case class HelloWorld2(greetings: String, name: String, option: Option[HelloWorld], subClass: List[Long])

class JsonConversionSuite extends munit.FunSuite {
  protected lazy val jsonConverter = new JsonConverter()

  test("Convert to List to Json") {
    assertEquals(jsonConverter.toJson(Array(1, 2)), "[1,2]")
    assertEquals(jsonConverter.toJson(List[Int]()), "[]")
    assertEquals(jsonConverter.toJson(List("hallo", "Welt")), "[\"hallo\",\"Welt\"]")
    assertEquals(jsonConverter.toJson(List(1, 2)), "[1,2]")
    assertEquals(jsonConverter.toJson(Array.empty[String]), "[]")
    assertEquals(jsonConverter.toJson(Array("hallo", "Welt")), "[\"hallo\",\"Welt\"]")
  }

  test("Convert Case Class to Json") {
    assertEquals(jsonConverter.toJson(HelloWorld("hello", "world")), "{\"greetings\":\"hello\",\"name\":\"world\"}")
    assertEquals(jsonConverter.toJson(HelloWorld("servus", "welt")), "{\"greetings\":\"servus\",\"name\":\"welt\"}")
    val jString: String = jsonConverter.toJson(HelloWorld2("servus", "welt", Option(HelloWorld("hello", "world")), List(1, 2, 3, 4, 5)))
    assertEquals(jString, "{\"greetings\":\"servus\",\"name\":\"welt\",\"option\":{\"greetings\":\"hello\",\"name\":\"world\"},\"subClass\":[1,2,3,4,5]}")
    val jString2: String = jsonConverter.toJson(HelloWorld2("servus", "welt", None, List.empty))
    assertEquals(jString2, "{\"greetings\":\"servus\",\"name\":\"welt\",\"option\":null,\"subClass\":[]}")
  }

  test("Convert to Json to List") {
    assertEquals(jsonConverter.toObject[List[String]]("[]"), List())
    assertEquals(jsonConverter.toObject[List[String]]("[\"hallo\",\"Welt\"]"), List("hallo", "Welt"))
    assertEquals(jsonConverter.toObject[List[Int]]("[1,2]"), List(1, 2))
    assertEquals(jsonConverter.toObject[Array[String]]("[\"hallo\",\"Welt\"]").last, Array("hallo", "Welt").last)
    assertEquals(jsonConverter.toObject[Array[Int]]("[1,2]").last, Array(1, 2).last)
  }

  test("Convert Json to Case Class") {
    assertEquals(jsonConverter.toObject[HelloWorld]("{\"name\":\"world\", \"greetings\":\"hello\"}"), HelloWorld("hello", "world"))
  }
}
