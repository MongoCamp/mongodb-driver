package dev.mongocamp.driver.mongodb.dao

import better.files.{ File, Resource }
import dev.mongocamp.driver.MongoImplicits
import dev.mongocamp.driver.mongodb.model.Student
import dev.mongocamp.driver.mongodb.server.LocalServer
import dev.mongocamp.driver.mongodb.test.UniversityDatabase
import dev.mongocamp.driver.mongodb.test.UniversityDatabase.{ GradeDAO, StudentDAO }
import munit.FunSuite

class StudentDAOSuite extends FunSuite with MongoImplicits {

  override def beforeAll(): Unit = {
    UniversityDatabase.LocalTestServer = LocalServer.fromPath("unit.test.local.mongo.server")
    StudentDAO.drop().result()
    StudentDAO.importJsonFile(File(Resource.getUrl("json/university/students.json"))).result()
    GradeDAO.drop().result()
    GradeDAO.importJsonFile(File(Resource.getUrl("json/university/grades.json"))).result()
  }

  override def afterAll(): Unit =
    UniversityDatabase.LocalTestServer.shutdown()

  test("StudentDAO should support count") {
    assertEquals(StudentDAO.name, "universityStudents")
    assertEquals(StudentDAO.databaseName, "mongocamp-unit-test")
  }

  test("StudentDAO should support count of students and grades") {
    val students: Long = StudentDAO.count()
    assertEquals(students, 200L)

    val grades: Long = GradeDAO.count()
    assertEquals(grades, 280L)
  }

  test("StudentDAO should support finding a student by name") {
    val student: Option[Student] = StudentDAO.find("name", "Aurelia Menendez")
    assert(student.isDefined)
    assertEquals(student.get.scores.size, 3)
  }

}
