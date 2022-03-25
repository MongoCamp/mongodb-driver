package dev.mongocamp.driver.mongodb.test

import better.files.File
import com.mongodb.client.model.changestream.OperationType
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.model._
import dev.mongocamp.driver.mongodb.{GridFSDAO, MongoDAO}
import com.typesafe.scalalogging.LazyLogging
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.Document
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.changestream.ChangeStreamDocument

object TestDatabase extends LazyLogging {
  val ImageDAOSourcePath = "src/test/resources/images/"
  val ImageDAOTargetPath = "/tmp/_download/"

  File(ImageDAOTargetPath).createIfNotExists()

  private val registry = fromProviders(classOf[Person], classOf[Friend], classOf[CodecTest], classOf[Book])

  val provider =
    DatabaseProvider.fromPath(configPath = "unit.test.mongo", registry = fromRegistries(registry))

  // provider.addChangeObserver(ChangeObserver(consumeDatabaseChanges))

  def consumeDatabaseChanges(changeStreamDocument: ChangeStreamDocument[Document]): Unit =
    if (changeStreamDocument.getOperationType != OperationType.INSERT)
      logger.info(
        "changed %s:%s with ID: %s".format(
          changeStreamDocument.getNamespace,
          changeStreamDocument.getOperationType,
          changeStreamDocument.getDocumentKey
        )
      )

  object PersonDAO extends MongoDAO[Person](provider, "people")

  object BookDAO extends MongoDAO[Book](provider, "books")

  object CodecDao extends MongoDAO[CodecTest](provider, "codec:codec-test")

  object PlacesDAO extends MongoDAO[Place](provider, "places")

  object ImageFilesDAO extends GridFSDAO(provider, "images")

}
