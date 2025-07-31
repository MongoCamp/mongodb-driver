package dev.mongocamp.driver.mongodb.sql

import com.mongodb.client.model.IndexOptions
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.model.Grade
import dev.mongocamp.driver.mongodb.model.Score
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.test.UniversityDatabase.GradeDAO
import java.sql.SQLException
import org.bson.types.ObjectId
import org.mongodb.scala.documentToUntypedDocument
import org.mongodb.scala.model.Sorts.ascending

class OtherSqlSuite extends BasePersonSuite {

  def prepareDatabase(): Unit = {
    GradeDAO.drop().result()
    GradeDAO
      .insertMany(
        List(
          Grade(new ObjectId(), 1, 2, List(Score(1.20, "test"), Score(120, "test1"))),
          Grade(new ObjectId(), 2, 4, List(Score(10, "test2"), Score(20, "test3"))),
          Grade(new ObjectId(), 3, 7, List(Score(10, "test4"), Score(20, "test5")))
        )
      )
      .result()
    GradeDAO.createIndex(ascending("studentId"), new IndexOptions().name("student_idx")).result()
  }

  test("drop collection") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("Drop table universityGrades;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    val grade = GradeDAO.count(Map("studentId" -> 1)).result()
    assertEquals(grade, 0L)
    val collections = TestDatabase.provider.collectionNames()
    assert(!collections.contains("universityGrades"))
  }

  test("catch sql error on converting sql") {
    prepareDatabase()
    var errorCaught = false
    try MongoSqlQueryHolder("blub from universityGrades;")
    catch {
      case _: SQLException =>
        errorCaught = true
    }
    assertEquals(errorCaught, true)
  }

  test("truncate collection") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("TRUNCATE TABLE universityGrades;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    val grade = GradeDAO.count(Map("studentId" -> 1)).result()
    assertEquals(grade, 0L)
    val collections = TestDatabase.provider.collectionNames()
    assert(collections.contains("universityGrades"))
  }

  test("create index") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("CREATE INDEX idx_name ON people (name);")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assertEquals(selectResponse.head.getStringValue("indexName"), "idx_name")
    val indices = TestDatabase.provider.collection("people").listIndexes().resultList()
    assert(indices.exists(_.getString("name") == "idx_name"))
  }

  test("create unique index") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("CREATE unique INDEX uidx_name ON people (email);")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assertEquals(selectResponse.head.getStringValue("indexName"), "uidx_name")
    val indices = TestDatabase.provider.collection("people").listIndexes().resultList()
    assert(indices.exists(_.getString("name") == "uidx_name"))
  }

  test("create index with multi") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("CREATE INDEX idx_multiname ON people (name, gender);")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assertEquals(selectResponse.head.getStringValue("indexName"), "idx_multiname")
    val indices = TestDatabase.provider.collection("people").listIndexes().resultList()
    assert(indices.exists(_.getString("name") == "idx_multiname"))
  }

  test("drop index") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("DROP INDEX universityGrades.student_idx;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assertEquals(selectResponse.head.getStringValue("indexName"), "student_idx")
    val indices = TestDatabase.provider.collection("universityGrades").listIndexes().resultList()
    assertEquals(indices.find(_.getString("name") == "student_idx"), None)
  }

  test("show tables") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("show tables;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.nonEmpty, true)
    val filteredDocuments = selectResponse.filter(
      d => d.getStringValue("name").equalsIgnoreCase("people")
    )
    assertEquals(filteredDocuments.head.getStringValue("name"), "people")
  }

  test("show databases") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("SHOW DATABASES;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assert(selectResponse.nonEmpty)
  }

  test("show schemas") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("SHOW SCHEMAS;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.nonEmpty, true)
  }

  test("show databases") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("SHOW databases;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.nonEmpty, true)
  }

  test("show schemas again") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("show SCHEMAS;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.nonEmpty, true)
  }

}
