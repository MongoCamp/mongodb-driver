package com.sfxcode.nosql.mongo.relation

import com.sfxcode.nosql.mongo.TestDatabase.PersonDAO
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.Person
import com.sfxcode.nosql.mongo.relation.RelationDemoDatabase._
import org.specs2.mutable.{ Before, Specification }

class RelationDemoSpec extends Specification with Before {

  sequential

  "Relations" should {

    "support OneToOne" in {

      val list: List[User] = UserDAO.find("name", "Massey Sears").resultList()

      list.size must be equalTo 1

      val user = list.head
      val login = user.login

      login must beSome[Login]

      login.get.email must be equalTo "masseysears@kog.com"

    }

    "support OneToMany" in {

      val list: List[User] = UserDAO.find("name", "Massey Sears").resultList()

      list.size must be equalTo 1

      val user = list.head

      val friends = user.friends

      friends.size must be equalTo 5

      friends.head.userId must be equalTo user.id

      friends.head.name must be equalTo "Katie Holden"

    }

  }

  override def before: Any = {

    try {
      UserDAO.drop().result()
      LoginDAO.drop().result()
      SimplePersonDAO.drop().result()
    } catch {
      case e: Exception =>
    }

    val personList = PersonDAO.find().resultList()
    personList.foreach(person => {
      UserDAO.insertOne(User(person.id, person.name, person.guid)).result()
      LoginDAO.insertOne(Login(person.guid, person.email, person.email.reverse)).result()
      person.friends.foreach(f => {
        SimplePersonDAO.insertOne(SimplePerson((person.id + 11) * (f.id + 3), f.name, person.id)).result()
      })
    })

  }
}
