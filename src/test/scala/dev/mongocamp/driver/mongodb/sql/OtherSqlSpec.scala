package dev.mongocamp.driver.mongodb.sql

import com.mongodb.client.model.IndexOptions
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.model.{ Grade, Score }
import dev.mongocamp.driver.mongodb.test.TestDatabase
import org.bson.types.ObjectId
import org.mongodb.scala.model.Sorts.ascending
import org.specs2.specification.BeforeEach

import java.sql.SQLException
import dev.mongocamp.driver.mongodb.json._
import io.circe.syntax._
import io.circe.generic.auto._

class OtherSqlSpec extends PersonSpecification with BeforeEach{
  sequential

  object GradeDAO extends MongoDAO[Grade](TestDatabase.provider, "universityGrades")

  override protected def before: Any = {
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
    this.GradeDAO.createIndex(ascending("studentId"), new IndexOptions().name("student_idx")).result()
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
  }

  "MongoSqlQueryHolder" should {

    "drop collection" in {
      val queryConverter = MongoSqlQueryHolder("Drop table universityGrades;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      val grade = GradeDAO.count(Map("studentId" -> 1)).result()
      grade mustEqual 0
      val collections = TestDatabase.provider.collectionNames()
      collections must not contain "universityGrades"
    }

    "catch sql error on converting sql" in {
      var errorCaught = false
      try {
        MongoSqlQueryHolder("blub from universityGrades;")
      } catch {
        case _: SQLException =>
          errorCaught = true
      }
      errorCaught mustEqual true
    }

    "truncate collection" in {
      val queryConverter = MongoSqlQueryHolder("TRUNCATE TABLE universityGrades;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      val grade = GradeDAO.count(Map("studentId" -> 1)).result()
      grade mustEqual 0
      val collections = TestDatabase.provider.collectionNames()
      collections.contains("universityGrades") must beTrue
    }

    "create index " in {
      val queryConverter = MongoSqlQueryHolder("CREATE INDEX idx_name ON people (name);")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getStringValue("indexName") mustEqual "idx_name"
      val indices = TestDatabase.provider.collection("people").listIndexes().resultList()
      indices.find(_.getString("name") == "idx_name") must beSome
    }

    "create unique index " in {
      val queryConverter = MongoSqlQueryHolder("CREATE unique INDEX uidx_name ON people (email);")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getStringValue("indexName") mustEqual "uidx_name"
      val indices = TestDatabase.provider.collection("people").listIndexes().resultList()
      indices.find(_.getString("name") == "uidx_name") must beSome
    }

    "create index with multi" in {
      val queryConverter = MongoSqlQueryHolder("CREATE INDEX idx_multiname ON people (name, gender);")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getStringValue("indexName") mustEqual "idx_multiname"
      val indices = TestDatabase.provider.collection("people").listIndexes().resultList()
      indices.find(_.getString("name") == "idx_multiname") must beSome
    }

    "drop index " in {
      val queryConverter = MongoSqlQueryHolder("DROP INDEX universityGrades.student_idx;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getStringValue("indexName") mustEqual "student_idx"
      val indices = TestDatabase.provider.collection("universityGrades").listIndexes().resultList()
      indices.find(_.getString("name") == "student_idx") must beNone
    }

    "show tables" in {
      val queryConverter = MongoSqlQueryHolder("show tables;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size must be greaterThanOrEqualTo(1)
      val filteredDocuments = selectResponse.filter(d => d.getStringValue("name").equalsIgnoreCase("people"))
      filteredDocuments.head.getStringValue("name") mustEqual "people"
    }

    "show databases" in {
      val queryConverter = MongoSqlQueryHolder("SHOW DATABASES;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size must be greaterThanOrEqualTo(1)
    }

    "show schemas" in {
      val queryConverter = MongoSqlQueryHolder("SHOW SCHEMAS;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size must be greaterThanOrEqualTo(1)
    }

    "show databases" in {
      val queryConverter = MongoSqlQueryHolder("SHOW  databases;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size must be greaterThanOrEqualTo(1)
    }

    "show schemas" in {
      val queryConverter = MongoSqlQueryHolder("show SCHEMAS;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size must be greaterThanOrEqualTo(1)
    }
  }

}
