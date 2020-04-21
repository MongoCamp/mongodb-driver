package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo.TestDatabase.printDatabaseStatus
import org.specs2.mutable.{ Before, Specification }

trait DatabaseSpec extends Specification with Before {

  sequential

  override def before: Any = printDatabaseStatus()

}
