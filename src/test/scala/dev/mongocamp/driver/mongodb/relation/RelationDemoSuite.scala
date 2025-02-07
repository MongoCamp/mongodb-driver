package dev.mongocamp.driver.mongodb.relation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.relation.RelationDemoDatabase._
import dev.mongocamp.driver.mongodb.test.TestDatabase.PersonDAO

class RelationDemoSuite extends munit.FunSuite {

  test("support OneToOne") {
    val list: List[User] = UserDAO.find("name", "Massey Sears").resultList()
    assertEquals(list.size, 1)

    val user  = list.head
    val login = user.login

    assert(login.isDefined)
    assertEquals(login.get.email, "masseysears@kog.com")
  }

  test("support OneToMany") {
    val list: List[User] = UserDAO.find("name", "Massey Sears").resultList()
    assertEquals(list.size, 1)

    val user    = list.head
    val friends = user.friends

    assertEquals(friends.size, 5)
    assertEquals(friends.head.userId, user.id)
    assertEquals(friends.head.name, "Katie Holden")
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    try {
      UserDAO.drop().result()
      LoginDAO.drop().result()
      SimplePersonDAO.drop().result()
    }
    catch {
      case e: Exception =>
    }

    val personList = PersonDAO.find().resultList()
    personList.foreach { person =>
      UserDAO.insertOne(User(person.id, person.name, person.guid)).result()
      LoginDAO.insertOne(Login(person.guid, person.email, person.email.reverse)).result()
      person.friends.foreach { f =>
        SimplePersonDAO.insertOne(SimplePerson((person.id + 11) * (f.id + 3), f.name, person.id)).result()
      }
    }
  }
}
