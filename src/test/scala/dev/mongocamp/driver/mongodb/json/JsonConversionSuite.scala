package dev.mongocamp.driver.mongodb.json
import dev.mongocamp.driver.mongodb.json.model.FooBar
import dev.mongocamp.driver.mongodb.json.model.HelloWorld
import dev.mongocamp.driver.mongodb.json.model.HelloWorld2
import dev.mongocamp.driver.mongodb.json.model.HelloWorld3
import io.circe.ParsingFailure
import org.typelevel.jawn.IncompleteParseException

class JsonConversionSuite extends munit.FunSuite {
  protected lazy val jsonConverter     = new JsonConverter()
  protected lazy val dropNullConverter = new JsonConverter(dropNullValues = true)

  test("Convert to List to Json") {
    assertEquals(jsonConverter.toJson(Array(1, 2)), "[1,2]")
    assertEquals(jsonConverter.toJson(List[Int]()), "[]")
    assertEquals(jsonConverter.toJson(List("hallo", "Welt")), "[\"hallo\",\"Welt\"]")
    assertEquals(jsonConverter.toJson(List(1, 2)), "[1,2]")
    assertEquals(jsonConverter.toJson(Array.empty[String]), "[]")
    assertEquals(jsonConverter.toJson(Array("hallo", "Welt")), "[\"hallo\",\"Welt\"]")
  }

  test("Convert Case Class with Any to Json") {
    val isScala3 = scala.util.Properties.versionNumberString.startsWith("3.")
    val jString3: String = if (isScala3) {
      jsonConverter.toJson(HelloWorld3("servus", "welt"))
    }
    else {
      jsonConverter.toJson(HelloWorld3("servus", "welt").asInstanceOf[Any])
    }
    assertEquals(jString3, "{\"greetings\":\"servus\",\"name\":\"welt\"}")
  }

  test("Convert Json to Case Class with Option using anyFormat") {
    assertEquals(jsonConverter.toJson(FooBar()), "{\"foo\":null,\"bar\":null}")
    assertEquals(dropNullConverter.toJson(FooBar()), "{}")
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

  test("Try to convert invalid strings") {
    assertEquals(jsonConverter.toObjectOption[List[String]](""), None)
    assertEquals(jsonConverter.toObjectOption[List[String]]("{}"), None)
    assertEquals(
      jsonConverter.toObjectRaw[List[String]](""),
      Left(value = ParsingFailure(message = "exhausted input", underlying = IncompleteParseException(msg = "exhausted input")))
    )
    assertEquals(jsonConverter.toObjectRaw[List[String]]("{}").left.get.getMessage, "DecodingFailure at : Got value '{}' with wrong type, expecting array")
  }

  test("Convert Json to Case Class") {
    assertEquals(jsonConverter.toObject[HelloWorld]("{\"name\":\"world\", \"greetings\":\"hello\"}"), HelloWorld("hello", "world"))
  }
}
