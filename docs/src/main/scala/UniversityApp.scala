import com.sfxcode.nosql.mongo.test.UniversityDatabase._
import com.sfxcode.nosql.mongo._

object UniversityApp extends App {

  val collections = provider.collections().resultList()

  //println(collections.tail.head.asPlainJson)

  // provider.databaseNames.foreach(info => println(info))

  println(StudentDAO)

}
