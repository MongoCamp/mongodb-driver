package dev.mongocamp.driver.mongodb.jdbc

import java.sql.ResultSet
import scala.collection.mutable.ArrayBuffer

class SelectJDBCSpec extends BaseJdbcSpec {

  "Jdbc Connection" should {

    "execute simple select" in {
      val stmt        = connection.createStatement()
      val result      = stmt.executeQuery("select id, guid, name, age, balance from people where age < 30 order by id asc")
      var i           = 0
      val arrayBuffer = ArrayBuffer[ResultSet]()
      while (result.next()) {
        i += 1
        arrayBuffer += result
      }
      arrayBuffer.size must beEqualTo(99)
      i must beEqualTo(99)
    }

    "execute prepared statement" in {
      val preparedStatement = connection.prepareStatement("select * from `mongocamp-unit-test`.people where age < ? order by id asc")
      preparedStatement.setLong(0, 30)
      val result = preparedStatement.executeQuery()
      var i           = 0
      val arrayBuffer = ArrayBuffer[ResultSet]()
      while (result.next()) {
        i += 1
        arrayBuffer += result
      }
      arrayBuffer.size must beEqualTo(99)
      i must beEqualTo(99)
    }

    "count on empty table" in {
      val stmt        = connection.createStatement()
      val result      = stmt.executeQuery("select count(*) as tmp, sum(age) from empty;")
      var i           = 0
      while (result.next()) {
        result.getInt("tmp") must beEqualTo(0)
        result.getInt("sum(age)") must beEqualTo(0)
        i += 1
      }
      i must beEqualTo(1)
    }

  }
}
