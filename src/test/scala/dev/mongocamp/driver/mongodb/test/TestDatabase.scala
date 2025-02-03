package dev.mongocamp.driver.mongodb.test

import better.files.File
import com.mongodb.client.model.changestream.OperationType
import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.json._
import dev.mongocamp.driver.mongodb.model._
import dev.mongocamp.driver.mongodb.{ GridFSDAO, MongoDAO }
import io.circe.generic.auto._
import org.mongodb.scala.Document
import org.mongodb.scala.model.changestream.ChangeStreamDocument

object TestDatabase extends LazyLogging {
  val ImageDAOSourcePath = "src/test/resources/images/"
  val ImageDAOTargetPath = "/tmp/_download/"

  File(ImageDAOTargetPath).createIfNotExists()


  val provider: DatabaseProvider = DatabaseProvider.fromPath(configPath = "unit.test.mongo")

  def consumeDatabaseChanges(changeStreamDocument: ChangeStreamDocument[Document]): Unit = {
    if (changeStreamDocument.getOperationType != OperationType.INSERT) {
      logger.info(
        "changed %s:%s with ID: %s".format(
          changeStreamDocument.getNamespace,
          changeStreamDocument.getOperationType,
          changeStreamDocument.getDocumentKey
        )
      )
    }
  }

  object PersonDocumentDAO extends MongoDAO[Document](provider, "people")

  object PersonDAO extends MongoDAO[Person](provider, "people")

  object BookDAO extends MongoDAO[Book](provider, "books")

  object CodecDao extends MongoDAO[CodecTest](provider, "codec:codec-test")

  object PlacesDAO extends MongoDAO[Place](provider, "places")

  object ImageFilesDAO extends GridFSDAO(provider, "images")

}
