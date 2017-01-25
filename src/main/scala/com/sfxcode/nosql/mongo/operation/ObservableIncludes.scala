package com.sfxcode.nosql.mongo.operation

import java.util.concurrent.TimeUnit

import com.sfxcode.nosql.mongo.Converter
import org.json4s.Formats
import org.mongodb.scala._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.reflect.Manifest

object ObservableIncludes extends ObservableIncludes

trait ObservableIncludes {

  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    override val debugString: (Document) => String = (doc) => doc.toJson
  }

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val debugString: (C) => String = (doc) => doc.toString

    def resultList[C](maxWait: Int = 10)(implicit formats: Formats, mf: Manifest[C]): List[C] =
      Await.result(observable.toFuture(), Duration(maxWait, TimeUnit.SECONDS)).toList.map(doc => {
        Converter.fromDocument[C](doc.asInstanceOf[Document])
      })

    def result[C](maxWait: Int = 10)(implicit formats: Formats, mf: Manifest[C]): Option[C] = {
      val list = Await.result(observable.toFuture(), Duration(maxWait, TimeUnit.SECONDS)).toList.map(doc => {
        Converter.fromDocument[C](doc.asInstanceOf[Document])
      })
      if (list.size == 1)
        Some(list.head)
      else
        None
    }

  }

  trait ImplicitObservable[C] {
    val observable: Observable[C]
    val debugString: (C) => String

    def results(maxWait: Int = 10): Seq[C] = Await.result(observable.toFuture(), Duration(maxWait, TimeUnit.SECONDS))

    def headResult(maxWait: Int = 10): C = Await.result(observable.head(), Duration(maxWait, TimeUnit.SECONDS))

    def printResults(initial: String = ""): Unit = {
      if (initial.length > 0) print(initial)
      results().foreach(res => println(debugString(res)))
    }

    def printHeadResult(initial: String = ""): Unit = println(s"${initial}${debugString(headResult())}")
  }

}
