package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb.model.{Grade, Score}
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.{GenericObservable, MongoDAO}
import org.bson.types.ObjectId
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach

class DeleteSqlSpec extends Specification with BeforeEach {
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

    "delete with where" in {
      val queryConverter = MongoSqlQueryHolder("DELETE FROM universityGrades WHERE studentId = 1;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.getLong("deletedCount") mustEqual 1
      val documents = GradeDAO.count().result()
      documents mustEqual 2
    }

    "delete all" in {
      val queryConverter = MongoSqlQueryHolder("DELETE FROM universityGrades;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.getLong("deletedCount") mustEqual 3
      val documents = GradeDAO.count().result()
      documents mustEqual 0
    }

    "delete all with or" in {
      val queryConverter = MongoSqlQueryHolder("DELETE FROM universityGrades WHERE classId = 4 or classId = 7;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.getLong("deletedCount") mustEqual 2
      val documents = GradeDAO.count().result()
      documents mustEqual 1
    }

  }
}
