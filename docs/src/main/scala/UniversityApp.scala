import dev.mongocamp.driver.mongodb.test.UniversityDatabase._
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.server.LocalServer

object UniversityApp extends App {

  val LocalTestServer = LocalServer()

  val collections = provider.collections().resultList()

  //println(collections.tail.head.asPlainJson)

  // provider.databaseNames.foreach(info => println(info))

  println(StudentDAO)

}
