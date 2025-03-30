package dev.mongocamp.driver.mongodb.dao

import dev.mongocamp.driver.mongodb.model.Person
import dev.mongocamp.driver.mongodb.test.TestDatabase.PersonDAO
import dev.mongocamp.driver.MongoImplicits
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PersonDAOSuite extends BasePersonSuite with MongoImplicits {

  test("support count") {
    val count: Long = PersonDAO.count()
    assertEquals(count, 200L)
  }

  test("support columnNames") {
    val columnNames = PersonDAO.columnNames(200)
    assertEquals(columnNames.size, 20)
  }

  test("support results") {
    val seq: Seq[Person] = PersonDAO.find()
    assertEquals(seq.size, 200)
  }

  test("support resultList") {
    val list: List[Person] = PersonDAO.find()
    assertEquals(list.size, 200)
  }

  test("support resultList with id") {
    val option: Option[Person] = PersonDAO.find("id", 42)
    assert(option.isDefined)
  }

  test("support asFuture") {
    val future: Future[Seq[Person]] = PersonDAO.find().asFuture()
    val mapped: Future[Seq[String]] = future.map(
      personSeq =>
        personSeq.map(
          p => p.name
        )
    )
    val names: Seq[String] = Await.result(mapped, Duration(10, TimeUnit.SECONDS))
    assertEquals(names.size, 200)
  }
}
