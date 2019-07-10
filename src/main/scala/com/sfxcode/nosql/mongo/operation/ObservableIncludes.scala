package com.sfxcode.nosql.mongo.operation

import java.util.concurrent.TimeUnit

import org.mongodb.scala._

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

object ObservableIncludes extends ObservableIncludes

trait ObservableIncludes {

  val DefaultMaxWait = 10

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val debugString: C => String = doc => doc.toString

    def resultList(maxWait: Int = DefaultMaxWait): List[C] =
      Await.result(asFuture(), Duration(maxWait, TimeUnit.SECONDS)).toList

    def result(maxWait: Int = DefaultMaxWait): Option[C] = {
      val list =
        Await.result(asFuture(), Duration(maxWait, TimeUnit.SECONDS)).toList
      list.headOption
    }

  }

  trait ImplicitObservable[C] {
    val observable: Observable[C]
    val debugString: C => String

    def asFuture(): Future[Seq[C]] = observable.toFuture()

    def results(maxWait: Int = DefaultMaxWait): Seq[C] =
      Await.result(asFuture(), Duration(maxWait, TimeUnit.SECONDS))

    def headResult(maxWait: Int = DefaultMaxWait): C =
      Await.result(observable.head(), Duration(maxWait, TimeUnit.SECONDS))

    def printResults(initial: String = ""): Unit = {
      if (initial.length > 0) print(initial)
      results().foreach(res => println(debugString(res)))
    }

    def printHeadResult(initial: String = ""): Unit =
      println(s"$initial${debugString(headResult())}")
  }

}
