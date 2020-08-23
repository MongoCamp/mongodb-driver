package com.sfxcode.nosql.mongo.dao

import java.util.concurrent.TimeUnit

import com.sfxcode.nosql.MongoImplicits
import com.sfxcode.nosql.mongo.test.TestDatabase.PersonDAO
import com.sfxcode.nosql.mongo.model.Person

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class PersonDAOSpec extends PersonSpecification with MongoImplicits {

  "PersonDAO" should {

    "support count" in {
      val count: Long = PersonDAO.count()
      count mustEqual 200
    }

    "support columnNames" in {
      val columnNames = PersonDAO.columnNames(100)
      columnNames.size mustEqual 18
    }

    "support results" in {
      val seq: Seq[Person] = PersonDAO.find()
      seq must haveSize(200)
    }

    "support resultList" in {
      val list: List[Person] = PersonDAO.find()
      list must haveSize(200)
    }

    "support resultList" in {
      val option: Option[Person] = PersonDAO.find("id", 42)
      option must not beEmpty
    }

    "support asFuture" in {
      val future: Future[Seq[Person]] = PersonDAO.find().asFuture()
      val mapped: Future[Seq[String]] = future.map(personSeq => personSeq.map(p => p.name))
      val names: Seq[String]          = Await.result(mapped, Duration(10, TimeUnit.SECONDS))
      names must haveSize(200)

    }

  }
}
