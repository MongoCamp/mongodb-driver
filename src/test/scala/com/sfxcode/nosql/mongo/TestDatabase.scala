package com.sfxcode.nosql.mongo

import com.mongodb.client.model.changestream.OperationType
import com.sfxcode.nosql.mongo.database.{ ChangeObserver, DatabaseProvider }
import com.sfxcode.nosql.mongo.model._
import com.typesafe.scalalogging.LazyLogging
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.Document
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.changestream.ChangeStreamDocument

object TestDatabase extends LazyLogging {

  private val registry           = fromProviders(classOf[Person], classOf[Friend], classOf[CodecTest])
  private val universityRegistry = fromProviders(classOf[Student], classOf[Score], classOf[Grade])

  val provider =
    DatabaseProvider.fromPath(configPath = "unit.test.mongo", registry = fromRegistries(registry, universityRegistry))

  // provider.addChangeObserver(ChangeObserver(consumeDatabaseChanges))

  def consumeDatabaseChanges(changeStreamDocument: ChangeStreamDocument[Document]): Unit =
    if (changeStreamDocument.getOperationType != OperationType.INSERT) {
      logger.info(
        "changed %s:%s with ID: %s".format(changeStreamDocument.getNamespace,
                                           changeStreamDocument.getOperationType,
                                           changeStreamDocument.getDocumentKey)
      )
    }

  object PersonDAO extends MongoDAO[Person](provider, "people")

  object BookDAO extends MongoDAO[Book](provider, "books")

  object CodecDao extends MongoDAO[CodecTest](provider, "codec:codec-test")

  object StudentDAO extends MongoDAO[Book](provider, "university-students")

  object GradeDAO extends MongoDAO[Book](provider, "university-grades")

}
