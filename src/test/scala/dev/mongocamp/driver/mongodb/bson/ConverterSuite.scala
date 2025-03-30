package dev.mongocamp.driver.mongodb.bson

import dev.mongocamp.driver.mongodb.model.Base
import dev.mongocamp.driver.mongodb.Converter

class ConverterSuite extends munit.FunSuite {

  test("Converter support Document roundtrip") {
    val base           = Base()
    val document       = Converter.toDocument(base)
    val integer: Int   = document.getInteger("int")
    val long: Long     = document.getLong("Long")
    val float: Float   = document.getDouble("float").floatValue()
    val double: Double = document.getDouble("double")
    val maybeBsonValue = document.get("option")

    assertEquals(integer, base.int)
    assertEquals(long, base.Long)
    assertEquals(float, base.float)
    assertEquals(double, base.double)
    assertEquals(document.getString("string"), base.string)
    assertEquals(document.getDate("date"), base.date)
    assertEquals(maybeBsonValue.isDefined, true)
    assertEquals(maybeBsonValue.get.asObjectId().getValue, base.option.get)
    assertEquals(base != null, true)
    assertEquals(base.isInstanceOf[Base], true)
  }

}
