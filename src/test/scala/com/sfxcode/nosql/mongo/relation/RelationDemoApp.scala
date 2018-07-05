package com.sfxcode.nosql.mongo.relation

// #import
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.relation.RelationDemoDatabase._

object RelationDemoApp extends App {

  NodeDAO.drop().headResult()
  NodeDAO.createIndexForField("id").headResult()
  NodeDAO.createIndexForField("parentId").headResult()

  NodeDAO.insertMany(List(Node(1, 0), Node(2, 1), Node(3, 1))).result()

  val node2 = NodeDAO.find("id", 2).headResult()

  println(node2)

  val node2Parent = node2.parent

  println(node2Parent)

  var node1 = NodeDAO.find("id", 1).headResult()

  println(node1.children)
  println(node1.children)

  val node3 = NodeDAO.find("id", 3).headResult()
  node3.setParent(node2)

  println(node1.children)

}
