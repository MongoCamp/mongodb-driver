package dev.mongocamp.driver.mongodb.operation

import com.typesafe.scalalogging.LazyLogging
import java.util.concurrent.TimeUnit
import org.mongodb.scala._
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.Future

object ObservableIncludes extends ObservableIncludes

trait ObservableIncludes {

  val DefaultMaxWait = 10

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val debugString: C => String = doc => doc.toString

  }

  trait ImplicitObservable[C] extends LazyLogging {
    val observable: Observable[C]
    val debugString: C => String

    def asFuture(): Future[Seq[C]] = observable.toFuture()

    def result(maxWait: Int = DefaultMaxWait): C = Await.result(observable.head(), Duration(maxWait, TimeUnit.SECONDS))

    def results(maxWait: Int = DefaultMaxWait): Seq[C] = Await.result(asFuture(), Duration(maxWait, TimeUnit.SECONDS))

    def resultList(maxWait: Int = DefaultMaxWait): List[C] = Await.result(asFuture(), Duration(maxWait, TimeUnit.SECONDS)).toList

    def resultOption(maxWait: Int = DefaultMaxWait): Option[C] = Await.result(observable.headOption(), Duration(maxWait, TimeUnit.SECONDS))

  }

}
