package dev.mongocamp.driver.mongodb.jdbc

import java.sql.ResultSet
import scala.collection.mutable.ArrayBuffer

class SelectJDBCSuite extends BaseJdbcSuite {

  test("execute simple select") {
    val stmt        = connection.createStatement()
    val result      = stmt.executeQuery("select id, guid, name, age, balance from people where age < 30 order by id asc")
    var i           = 0
    val arrayBuffer = ArrayBuffer[ResultSet]()
    while (result.next()) {
      assertEquals(result.getLong(1),result.getLong("id"))
      assertEquals(result.getString(2),result.getString("guid"))
      assertEquals(result.getString(3),result.getString("name"))
      assertEquals(result.getInt(4),result.getInt("age"))
      assertEquals(result.getDouble(5),result.getDouble("balance"))
      i += 1
      arrayBuffer += result
    }
    assertEquals(arrayBuffer.size, 99)
    assertEquals(i, 99)
  }

  test("execute prepared statement") {
    val preparedStatement = connection.prepareStatement("select * from `mongocamp-unit-test`.people where age < ? order by id asc")
    preparedStatement.setLong(1, 30)
    val result      = preparedStatement.executeQuery()
    var i           = 0
    val arrayBuffer = ArrayBuffer[ResultSet]()
    while (result.next()) {
      i += 1
      arrayBuffer += result
    }
    assertEquals(arrayBuffer.size, 99)
    assertEquals(i, 99)
  }

  test("count on empty table") {
    val stmt   = connection.createStatement()
    val result = stmt.executeQuery("select count(*) as tmp, sum(age) from empty;")
    var i      = 0
    while (result.next()) {
      assertEquals(result.getInt("tmp"), 0)
      assertEquals(result.getInt("sum(age)"), 0)
      i += 1
    }
    assertEquals(i, 1)
  }

}
