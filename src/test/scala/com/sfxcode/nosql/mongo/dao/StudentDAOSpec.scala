package com.sfxcode.nosql.mongo.dao

import better.files.{File, Resource}
import com.sfxcode.nosql.MongoImplicits
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.Student
import com.sfxcode.nosql.mongo.server.LocalServer
import com.sfxcode.nosql.mongo.test.UniversityDatabase
import com.sfxcode.nosql.mongo.test.UniversityDatabase.{GradeDAO, StudentDAO}
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragments
import org.specs2.specification.{AfterAll, AfterSpec, BeforeAll}

class StudentDAOSpec extends Specification with BeforeAll with AfterAll with MongoImplicits {
  sequential

  override def beforeAll(): Unit = {
    UniversityDatabase.LocalTestServer = LocalServer.fromPath("unit.test.local.mongo.server")
    StudentDAO.drop().result()
    StudentDAO.importJsonFile(File(Resource.getUrl("json/university/students.json"))).result()
    GradeDAO.drop().result()
    GradeDAO.importJsonFile(File(Resource.getUrl("json/university/grades.json"))).result()
  }

  override def afterAll(): Unit =
    UniversityDatabase.LocalTestServer.shutdown()

  "StudentDAO" should {
    "support count" in {
      StudentDAO.name mustEqual "university-students"
      StudentDAO.databaseName mustEqual "simple-mongo-unit-test"
    }

    "support count" in {
      val students: Long = StudentDAO.count()
      students mustEqual 200

      val grades: Long = GradeDAO.count()
      grades mustEqual 280

    }

    "support count" in {
      val student: Option[Student] = StudentDAO.find("name", "Aurelia Menendez")
      student.get.scores must haveSize(3)

    }
  }

}
