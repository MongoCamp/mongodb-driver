import com.sfxcode.nosql.mongo.test.UniversityDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.server.LocalServer

object UniversityApp extends App {

  val LocalTestServer = LocalServer()

  val collections = provider.collections().resultList()

  //println(collections.tail.head.asPlainJson)

  // provider.databaseNames.foreach(info => println(info))

  println(StudentDAO)

}
