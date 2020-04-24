package com.sfxcode.nosql.mongo.dao

import better.files.{File, Resource}
import com.sfxcode.nosql.mongo.TestDatabase.{GradeDAO, StudentDAO}
import com.sfxcode.nosql.mongo._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class StudentDAOSpec extends Specification with BeforeAll {
  sequential

  override def beforeAll(): Unit = {
    StudentDAO.drop().result()
    StudentDAO.importJsonFile(File(Resource.getUrl("json/university/students.json"))).result()
    GradeDAO.drop().result()
    GradeDAO.importJsonFile(File(Resource.getUrl("json/university/grades.json"))).result()
  }

  "StudentDAO" should {
    "support count" in {
      val students: Long = StudentDAO.count().result()
      students mustEqual 200

      val grades: Long = GradeDAO.count().result()
      grades mustEqual 280

    }
  }
}
