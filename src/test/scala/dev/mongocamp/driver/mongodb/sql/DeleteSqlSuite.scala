package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb.GenericObservable
import dev.mongocamp.driver.mongodb.model.{ Grade, Score }
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.test.UniversityDatabase.GradeDAO
import org.bson.types.ObjectId

class DeleteSqlSuite extends munit.FunSuite {

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
  }

  test("delete with where") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("DELETE FROM universityGrades WHERE studentId = 1;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    assertEquals(selectResponse.head.getLong("deletedCount").toLong, 1L)
    val documents = GradeDAO.count().result()
    assertEquals(documents, 2L)
  }

  test("delete all") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("DELETE FROM universityGrades;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    assertEquals(selectResponse.head.getLong("deletedCount").toLong, 3L)
    val documents = GradeDAO.count().result()
    assertEquals(documents, 0L)
  }

  test("delete all with or") {
    prepareDatabase()
    val queryConverter = MongoSqlQueryHolder("DELETE FROM universityGrades WHERE classId = 4 or classId = 7;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"), true)
    assertEquals(selectResponse.head.getLong("deletedCount").toLong, 2L)
    val documents = GradeDAO.count().result()
    assertEquals(documents, 1L)
  }

}
