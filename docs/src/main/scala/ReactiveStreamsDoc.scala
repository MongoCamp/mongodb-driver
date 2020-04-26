import java.util.concurrent.TimeUnit

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.Person
import com.sfxcode.nosql.mongo.test.TestDatabase.{ImageFilesDAO, PersonDAO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object ReactiveStreamsDoc {

  // #implicit_result_conversion
  val imagesCount: Long      = ImageFilesDAO.count()
  val seq: Seq[Person]       = PersonDAO.find()
  val list: List[Person]     = PersonDAO.find()
  val option: Option[Person] = PersonDAO.find("id", 42)
  // #implicit_result_conversion

  // #as_future
  val future: Future[Seq[Person]] = PersonDAO.find().asFuture()
  val mapped: Future[Seq[String]] =
    future.map(personSeq => personSeq.map(p => p.name))

  val duration           = Duration(10, TimeUnit.SECONDS)
  val names: Seq[String] = Await.result(mapped, duration)
  // #as_future

}
