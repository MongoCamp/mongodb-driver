package dev.mongocamp.driver.mongodb.relation

// #import
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.relation.RelationDemoDatabase._

object RelationDemoApp extends App {

  NodeDAO.drop().result()
  NodeDAO.createIndexForField("id").result()
  NodeDAO.createIndexForField("parentId").result()

  NodeDAO.insertMany(List(Node(1, 0), Node(2, 1), Node(3, 1))).result()

  val node2 = NodeDAO.find("id", 2).result()

  println(node2)

  val node2Parent = node2.parent

  println(node2Parent)

  var node1 = NodeDAO.find("id", 1).result()

  println(node1.children)
  println(node1.children)

  val node3 = NodeDAO.find("id", 3).result()
  node3.setParent(node2)

  println(node1.children)

}
