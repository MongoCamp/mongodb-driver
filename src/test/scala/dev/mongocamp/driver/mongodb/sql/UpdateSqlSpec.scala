package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb.model.{ Grade, Score }
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb._
import org.bson.types.ObjectId
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach
import dev.mongocamp.driver.mongodb.json._
import io.circe.syntax._
import io.circe.generic.auto._

class UpdateSqlSpec extends Specification with BeforeEach {
  sequential

  object GradeDAO extends MongoDAO[Grade](TestDatabase.provider, "universityGrades")

  override def before(): Unit = {
    this.GradeDAO.drop().result()
    this.GradeDAO
      .insertMany(
        List(
          Grade(new ObjectId(), 1, 2, List(Score(1.20, "test"), Score(120, "test1"))),
          Grade(new ObjectId(), 2, 4, List(Score(10, "test2"), Score(20, "test3"))),
          Grade(new ObjectId(), 3, 7, List(Score(10, "test4"), Score(20, "test5")))
        )
      )
      .result()
  }

  "MongoSqlQueryHolder" should {

    "update single document" in {
      val queryConverter = MongoSqlQueryHolder("UPDATE universityGrades SET column1 = 'hello', classId = 47 WHERE studentId = 1;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.getLong("modifiedCount") mustEqual 1
      selectResponse.head.getLong("matchedCount") mustEqual 1
      val grade = GradeDAO.find(Map("studentId" -> 1)).result()
      grade.classId mustEqual 47
      val documents = TestDatabase.provider.dao("universityGrades").find(Map("studentId" -> 1)).result()
      documents.getLong("classId") mustEqual 47
      documents.getStringValue("column1") mustEqual "hello"
    }

    "update all" in {
      val queryConverter = MongoSqlQueryHolder("UPDATE universityGrades SET column1 = 'hello', classId = 47;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.getLong("modifiedCount") mustEqual 3
      selectResponse.head.getLong("matchedCount") mustEqual 3
      val grade = GradeDAO.find(Map("studentId" -> 1)).result()
      grade.classId mustEqual 47
      val documents = TestDatabase.provider.dao("universityGrades").find(Map("studentId" -> 1)).result()
      documents.getLong("classId") mustEqual 47
      documents.getStringValue("column1") mustEqual "hello"
    }

    "update multiple with or" in {
      val queryConverter = MongoSqlQueryHolder("UPDATE universityGrades SET column1 = 'hello', classId = 47 WHERE classId = 4 or classId = 7;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.getLong("modifiedCount") mustEqual 2
      selectResponse.head.getLong("matchedCount") mustEqual 2
      val grade = GradeDAO.find(Map("studentId" -> 2)).result()
      grade.classId mustEqual 47
      val documents = TestDatabase.provider.dao("universityGrades").find(Map("studentId" -> 2)).result()
      documents.getLong("classId") mustEqual 47
      documents.getStringValue("column1") mustEqual "hello"
    }

  }
}
