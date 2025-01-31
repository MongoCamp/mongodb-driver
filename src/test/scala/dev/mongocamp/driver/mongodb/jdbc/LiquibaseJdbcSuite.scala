package dev.mongocamp.driver.mongodb.jdbc

import com.typesafe.scalalogging.LazyLogging
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.{ Contexts, LabelExpression, Liquibase }

import scala.jdk.CollectionConverters._
import scala.language.implicitConversions

class LiquibaseJdbcSuite extends BaseJdbcSuite with LazyLogging {

  test("Jdbc Connection should migrate database with liquibase") {
    val jdbcConnection       = new JdbcConnection(connection)
    val liquibase: Liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConnection)
    val contexts             = new Contexts()
    val unrunChangesets      = liquibase.listUnrunChangeSets(contexts, new LabelExpression())
    val changes              = unrunChangesets.asScala.toList
    if (changes.isEmpty) {
      logger.info("liquibase - nothing to update")
      assert(true)
    }
    logger.info("liquibase - %s changesets to update".format(changes))
    try {
      liquibase.update(contexts)
      assert(true)
    }
    catch {
      case e: LiquibaseException =>
        logger.error(e.getMessage, e)
        assert(false)
    }
  }

}
