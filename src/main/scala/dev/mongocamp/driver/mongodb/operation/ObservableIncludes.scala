package dev.mongocamp.driver.mongodb.operation

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.database.ConfigHelper
import java.util.concurrent.TimeUnit
import org.mongodb.scala._
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Await
import scala.concurrent.Future

object ObservableIncludes extends ObservableIncludes

trait ObservableIncludes extends ConfigHelper {

  lazy val DefaultMaxWaitDuration: FiniteDuration = durationConfig("dev.mongocamp.mongodb.operation", "maxWait", FiniteDuration(10, TimeUnit.SECONDS))

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val debugString: C => String = doc => doc.toString
  }

  trait ImplicitObservable[C] extends LazyLogging {
    val observable: Observable[C]
    val debugString: C => String

    def asFuture(): Future[Seq[C]] = observable.toFuture()

    def result(): C = result(DefaultMaxWaitDuration)
    def result(maxWait: Int): C = result(Duration(maxWait, TimeUnit.SECONDS))
    def result(maxWait: Duration): C = Await.result(observable.head(), maxWait)

    def results(): Seq[C] = results(DefaultMaxWaitDuration)
    def results(maxWait: Int): Seq[C] = results(Duration(maxWait, TimeUnit.SECONDS))
    def results(maxWait: Duration): Seq[C] = Await.result(asFuture(), maxWait)

    def resultList(): List[C] = resultList(DefaultMaxWaitDuration)
    def resultList(maxWait: Int): List[C] = resultList(Duration(maxWait, TimeUnit.SECONDS))
    def resultList(maxWait: Duration): List[C] = Await.result(asFuture(), maxWait).toList

    def resultOption(): Option[C] = resultOption(DefaultMaxWaitDuration)
    def resultOption(maxWait: Int): Option[C] = resultOption(Duration(maxWait, TimeUnit.SECONDS))
    def resultOption(maxWait: Duration): Option[C] = Await.result(observable.headOption(), maxWait)

  }

}
