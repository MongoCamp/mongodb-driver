package dev.mongocamp.driver.mongodb.sql

import net.sf.jsqlparser.statement.create.index.CreateIndex

object SQLCommandType extends Enumeration {

  type SQLCommandType = Value

  val Delete, Select, Update, Insert, CreateIndex, DropTable, DropIndex, DropDatabase, ShowDatabases, ShowTables, Execute, AlterTable, CreateTable = Value

}
