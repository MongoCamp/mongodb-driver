package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.model.Grade
import dev.mongocamp.driver.mongodb.model.Score
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.test.UniversityDatabase.GradeDAO
import munit.FunSuite
import org.bson.types.ObjectId

class UpdateSqlSuite extends FunSuite {

  override def beforeEach(context: BeforeEach): Unit = {
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
  }

  test("update single document") {
    val queryConverter = MongoSqlQueryHolder("UPDATE universityGrades SET column1 = 'hello', classId = 47 WHERE studentId = 1;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    assertEquals(selectResponse.head.getLong("modifiedCount").toInt, 1)
    assertEquals(selectResponse.head.getLong("matchedCount").toInt, 1)
    val grade = GradeDAO.find(Map("studentId" -> 1)).result()
    assertEquals(grade.classId.toInt, 47)
    val documents = TestDatabase.provider.dao("universityGrades").find(Map("studentId" -> 1)).result()
    assertEquals(documents.getLong("classId").toInt, 47)
    assertEquals(documents.getStringValue("column1"), "hello")
  }

  test("update all") {
    val queryConverter = MongoSqlQueryHolder("UPDATE universityGrades SET column1 = 'hello', classId = 47;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    assertEquals(selectResponse.head.getLong("modifiedCount").toInt, 3)
    assertEquals(selectResponse.head.getLong("matchedCount").toInt, 3)
    val grade = GradeDAO.find(Map("studentId" -> 1)).result()
    assertEquals(grade.classId.toInt, 47)
    val documents = TestDatabase.provider.dao("universityGrades").find(Map("studentId" -> 1)).result()
    assertEquals(documents.getLong("classId").toInt, 47)
    assertEquals(documents.getStringValue("column1"), "hello")
  }

  test("update multiple with or") {
    val queryConverter = MongoSqlQueryHolder("UPDATE universityGrades SET column1 = 'hello', classId = 47 WHERE classId = 4 or classId = 7;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    assertEquals(selectResponse.head.getLong("modifiedCount").toInt, 2)
    assertEquals(selectResponse.head.getLong("matchedCount").toInt, 2)
    val grade = GradeDAO.find(Map("studentId" -> 2)).result()
    assertEquals(grade.classId.toInt, 47)
    val documents = TestDatabase.provider.dao("universityGrades").find(Map("studentId" -> 2)).result()
    assertEquals(documents.getLong("classId").toInt, 47)
    assertEquals(documents.getStringValue("column1"), "hello")
  }

}
