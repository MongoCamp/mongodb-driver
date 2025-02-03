package dev.mongocamp.driver.mongodb.jdbc

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.GenericObservable
import dev.mongocamp.driver.mongodb.test.TestDatabase
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.{Contexts, LabelExpression, Liquibase}
import dev.mongocamp.driver.mongodb.json._

import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions

class LiquibaseJdbcSuite extends BaseJdbcSuite with LazyLogging {

  override def beforeAll(): Unit = {
    TestDatabase.provider.dropDatabase("mongocamp-unit-test").results()
    super.beforeAll()
  }

  test("Jdbc Connection should migrate database with liquibase") {
    val jdbcConnection       = new JdbcConnection(connection)
    val liquibase: Liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConnection)
    val contexts             = new Contexts()
    val unrunChangesets      = liquibase.listUnrunChangeSets(contexts, new LabelExpression())
    val changes              = unrunChangesets.asScala.toList
    assert(changes.nonEmpty)
    logger.info("liquibase - %s changesets to update".format(changes))
    try {
      liquibase.update(contexts)
    }
    catch {
      case e: LiquibaseException =>
        logger.error(e.getMessage, e)
        assert(false)
    }
    val unrunChangesetsAfter = liquibase.listUnrunChangeSets(contexts, new LabelExpression())
    assert(unrunChangesetsAfter.asScala.isEmpty)
  }

}
