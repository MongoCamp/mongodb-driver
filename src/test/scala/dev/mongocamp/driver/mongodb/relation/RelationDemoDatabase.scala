package dev.mongocamp.driver.mongodb.relation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.json._
import io.circe.generic.auto._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.result.UpdateResult

object RelationDemoDatabase {

  case class Node(id: Long, parentId: Long, _id: ObjectId = new ObjectId()) extends Relations {

    def parent: Option[Node] =
      relatedRecordForOneToOne(NodeDAO.parentRelation, parentId)

    def setParent(node: Node): UpdateResult = {
      NodeDAO.parentRelation.removeFromCache(parentId)
      NodeDAO.childrenRelation.removeFromCache(node.id)
      val newNode = copy(parentId = node.id)
      NodeDAO.replaceOne(newNode).result()
    }

    def children: List[Node] =
      relatedRecordForOneToMany(NodeDAO.childrenRelation, id)
  }

  // #region user_class
  case class User(id: Long, name: String, loginId: String) extends Relations {

    def login: Option[Login] = relatedRecordForOneToOne(UserDAO.loginRelation, loginId)

    def friends: List[SimplePerson] = relatedRecordForOneToMany(UserDAO.friendsRelation, id)

  }
  // #endregion user_class

  case class Login(id: String, email: String, password: String)

  case class SimplePerson(id: Long, name: String, userId: Long)

  // #region user_dao
  object UserDAO extends MongoDAO[User](provider, "user") {
    lazy val loginRelation: OneToOneRelationship[Login]           = OneToOneRelationship(LoginDAO, "id")
    lazy val friendsRelation: OneToManyRelationship[SimplePerson] = OneToManyRelationship(SimplePersonDAO, "userId")
  }
  // #endregion user_dao

  object LoginDAO extends MongoDAO[Login](provider, "login")

  object SimplePersonDAO extends MongoDAO[SimplePerson](provider, "friend")

  val provider: DatabaseProvider = DatabaseProvider.fromPath("unit.test.mongo")

  object NodeDAO extends MongoDAO[Node](provider, "nodes") {
    lazy val parentRelation: OneToOneRelationship[Node]    = OneToOneRelationship(this, "id")
    lazy val childrenRelation: OneToManyRelationship[Node] = OneToManyRelationship(this, "parentId")
  }

}
