package com.sfxcode.nosql.mongo.relation

import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.{ MongoDAO, _ }
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.result.UpdateResult

object RelationDemoDatabase {

  case class Node(id: Long, parentId: Long, _id: ObjectId = new ObjectId()) extends Relations {

    def parent: Option[Node] = {
      relatedRecordForOneToOne(NodeDAO.parentRelation, parentId)
    }

    def setParent(node: Node): UpdateResult = {
      NodeDAO.parentRelation.removeFromCache(parentId)
      NodeDAO.childrenRelation.removeFromCache(node.id)
      val newNode = copy(parentId = node.id)
      NodeDAO.updateResult(newNode)
    }

    def children: List[Node] = {
      relatedRecordForOneToMany(NodeDAO.childrenRelation, id)
    }
  }

  // #user_class
  case class User(id: Long, name: String, loginId: String) extends Relations {

    def login: Option[Login] = relatedRecordForOneToOne(UserDAO.loginRelation, loginId)

    def friends: List[Friend] = relatedRecordForOneToMany(UserDAO.friendsRelation, id)

  }
  // #user_class

  case class Login(id: String, email: String, password: String)

  case class Friend(id: Long, name: String, userId: Long)

  // #user_dao
  object UserDAO extends MongoDAO[User](database, "user") {
    lazy val loginRelation = OneToOneRelationship(LoginDAO, "id")
    lazy val friendsRelation = OneToManyRelationship(FriendDAO, "userId")
  }
  // #user_dao

  object LoginDAO extends MongoDAO[Login](database, "login")

  object FriendDAO extends MongoDAO[Friend](database, "friend")

  // #registry
  private val registry = fromProviders(classOf[Node], classOf[User], classOf[Login], classOf[Friend])

  val database = DatabaseProvider("relation_test", registry)

  object NodeDAO extends MongoDAO[Node](database, "nodes") {
    lazy val parentRelation = OneToOneRelationship(this, "id")
    lazy val childrenRelation = OneToManyRelationship(this, "parentId")
  }

}
