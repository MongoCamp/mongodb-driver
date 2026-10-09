package dev.mongocamp.driver.mongodb

import java.util.UUID
import org.bson.BsonDocument

class FilterSuite extends munit.FunSuite {

  private val uuid1 = UUID.fromString("1c9b6e2a-3f4d-4c5e-8a7b-9d0e1f2a3b4c")
  private val uuid2 = UUID.fromString("2d0c7f3b-4a5e-4d6f-9b8c-0e1f2a3b4c5d")

  test("valueFilter converts single value with BsonConverter") {
    assertEquals(Filter.valueFilter("uuid", uuid1).toBsonDocument, BsonDocument.parse(s"""{"uuid": "$uuid1"}"""))

    val localDate = new org.joda.time.LocalDate(2024, 2, 29)
    val millis    = localDate.toDateTimeAtStartOfDay.getMillis
    assertEquals(Filter.valueFilter("date", localDate).toBsonDocument, BsonDocument.parse(s"""{"date": {"$$date": $millis}}"""))
  }

  test("valueFilter converts list values with BsonConverter") {
    assertEquals(Filter.valueFilter("uuid", List(uuid1, uuid2)).toBsonDocument, BsonDocument.parse(s"""{"uuid": {"$$in": ["$uuid1", "$uuid2"]}}"""))
  }

  test("valueFilter converts set values with BsonConverter") {
    assertEquals(Filter.valueFilter("uuid", Set(uuid1)).toBsonDocument, BsonDocument.parse(s"""{"uuid": {"$$in": ["$uuid1"]}}"""))
  }

}
