package com.sfxcode.nosql.mongo.relation

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
      UserDAO.drop().headResult()
      LoginDAO.drop().headResult()
      FriendDAO.drop().headResult()
    } catch {
      case e: Exception =>
    }

    val personList = Person.personList.take(10)
    personList.foreach(person => {
      UserDAO.insertOne(User(person.id, person.name, person.guid)).headResult()
      LoginDAO.insertOne(Login(person.guid, person.email, person.email.reverse)).headResult()
      person.friends.foreach(f => {
        FriendDAO.insertOne(Friend((person.id + 11) * (f.id + 3), f.name, person.id)).headResult()
      })
    })

  }
}
