package dev.mongocamp.driver.mongodb.jdbc

import com.typesafe.scalalogging.LazyLogging
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.{Contexts, LabelExpression, Liquibase}

import scala.jdk.CollectionConverters._
import scala.language.implicitConversions


class LiquibaseJdbcSpec extends BaseJdbcSpec with LazyLogging {

  "Jdbc Connection" should {

    "migrate database with liquibase" in {
      val jdbcConnection = new JdbcConnection(connection)
      val liquibase: Liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConnection )
      val contexts = new Contexts()
      val unrunChangesets = liquibase.listUnrunChangeSets(contexts, new LabelExpression())
      val changes = unrunChangesets.asScala.toList
      if (changes.isEmpty) {
        logger.info("liquibase - nothing to update")
        true must beTrue
      }
      logger.info("liquibase - %s changesets to update".format(changes))
      try {
        liquibase.update(contexts)
        true must beTrue
      }
      catch {
        case e: LiquibaseException =>
          logger.error(e.getMessage, e)
          false must beTrue
      }

    }

  }
}
