package com.sfxcode.nosql.mongo.test

import better.files.File
import com.mongodb.client.model.changestream.OperationType
import com.sfxcode.nosql.mongo.database.DatabaseProvider
import com.sfxcode.nosql.mongo.model._
import com.sfxcode.nosql.mongo.{GridFSDAO, MongoDAO}
import com.typesafe.scalalogging.LazyLogging
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.Document
import org.mongodb.scala.model.changestream.ChangeStreamDocument
import org.mongodb.scala.bson.codecs.Macros._

object TestDatabase extends LazyLogging {
  val ImageDAOSourcePath = "src/test/resources/images/"
  val ImageDAOTargetPath = "/tmp/_download/"

  File(ImageDAOTargetPath).createIfNotExists()

  private val registry           = fromProviders(classOf[Person], classOf[Friend], classOf[CodecTest])
  private val universityRegistry = fromProviders(classOf[Student], classOf[Score], classOf[Grade])

  val provider =
    DatabaseProvider.fromPath(configPath = "unit.test.mongo", registry = fromRegistries(registry, universityRegistry))

  // provider.addChangeObserver(ChangeObserver(consumeDatabaseChanges))

  def consumeDatabaseChanges(changeStreamDocument: ChangeStreamDocument[Document]): Unit =
    if (changeStreamDocument.getOperationType != OperationType.INSERT) {
      logger.info(
        "changed %s:%s with ID: %s".format(
          changeStreamDocument.getNamespace,
          changeStreamDocument.getOperationType,
          changeStreamDocument.getDocumentKey
        )
      )
    }

  object PersonDAO extends MongoDAO[Person](provider, "people")

  object BookDAO extends MongoDAO[Book](provider, "books")

  object CodecDao extends MongoDAO[CodecTest](provider, "codec:codec-test")

  object StudentDAO extends MongoDAO[Book](provider, "university-students")

  object GradeDAO extends MongoDAO[Book](provider, "university-grades")

  object ImageFilesDAO extends GridFSDAO(provider, "images")

}
