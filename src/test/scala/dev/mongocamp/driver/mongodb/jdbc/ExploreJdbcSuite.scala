package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb.jdbc.resultSet.MongoDbResultSet
import dev.mongocamp.driver.mongodb.BuildInfo
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.sql.Types

class ExploreJdbcSuite extends BaseJdbcSuite {

  val schemaPattern: String = "mongocamp-unit-test$"

  test("Jdbc Connection should get table names") {
    val tableNames       = connection.getMetaData.getTables("%", schemaPattern, "", Array.empty)
    var tables           = 0
    var tablePersonFound = false
    while (tableNames.next()) {
      tableNames.getString("TABLE_NAME") match {
        case "people" =>
          tablePersonFound = true
          assertEquals(tableNames.getString("TYPE_CAT"), "mongodb")
          assertEquals(tableNames.getString("REMARKS"), "COLLECTION")
          assertEquals(tableNames.getString("TABLE_TYPE"), "TABLE")
          assertEquals(tableNames.getString("TABLE_SCHEM"), "mongocamp-unit-test")
        case _ =>
      }
      tables += 1
    }
    assert(tables >= 1)
    val columnNames = connection.getMetaData.getColumns("%", schemaPattern, "people", "")
    var columns     = 0
    while (columnNames.next()) {
      assertEquals(columnNames.getString("TABLE_CAT"), "mongodb")
      assertEquals(columnNames.getString("TABLE_NAME"), "people")
      assertEquals(columnNames.getString("TABLE_SCHEM"), "mongocamp-unit-test")
      val KeyDataType = "DATA_TYPE"
      columnNames.getString("COLUMN_NAME") match {
        case "_id" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.VARCHAR)
        case "id" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.BIGINT)
          assertEquals(columnNames.getInt("DECIMAL_DIGITS"), 0)
        case "guid" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.LONGVARCHAR)
        case "isActive" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.BOOLEAN)
        case "balance" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.DOUBLE)
          assertEquals(columnNames.getInt("DECIMAL_DIGITS"), Int.MaxValue)
        case "registered" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.DATE)
        case "tags" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.ARRAY)
        case "friends" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.ARRAY)
        case "bestFriend" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.JAVA_OBJECT)
        case _ =>
      }
      columns += 1
    }
    assertEquals(columns, 20)
    assert(tablePersonFound)
  }

  test("allProceduresAreCallable should return false") {
    assertEquals(connection.getMetaData.allProceduresAreCallable(), false)
  }

  test("allTablesAreSelectable should return false") {
    assertEquals(connection.getMetaData.allTablesAreSelectable(), false)
  }

  test("getURL should return connection string") {
    assertEquals(connection.getMetaData.getURL, "mongodb://127.0.0.1:27017/mongocamp-unit-test")
  }

  test("getUserName should return user name") {
    assertEquals(connection.getMetaData.getUserName, "not set")
  }

  test("isReadOnly should return false") {
    assertEquals(connection.getMetaData.isReadOnly, false)
  }

  test("nullsAreSortedHigh should return false") {
    assertEquals(connection.getMetaData.nullsAreSortedHigh(), false)
  }

  test("nullsAreSortedLow should return false") {
    assertEquals(connection.getMetaData.nullsAreSortedLow(), false)
  }

  test("nullsAreSortedAtStart should return false") {
    assertEquals(connection.getMetaData.nullsAreSortedAtStart(), false)
  }

  test("nullsAreSortedAtEnd should return false") {
    assertEquals(connection.getMetaData.nullsAreSortedAtEnd(), false)
  }

  test("getDatabaseProductName should return mongodb") {
    assertEquals(connection.getMetaData.getDatabaseProductName, "mongodb")
  }

  test("getDatabaseProductVersion should return version") {
    assertNotEquals(connection.getMetaData.getDatabaseProductVersion, null)
  }

  test("getDriverName should return driver name") {
    assertEquals(connection.getMetaData.getDriverName, BuildInfo.name)
  }

  test("getDriverVersion should return driver version") {
    assertEquals(connection.getMetaData.getDriverVersion, BuildInfo.version)
  }

  test("getDriverMajorVersion should return major version") {
    assertEquals(connection.getMetaData.getDriverMajorVersion, BuildInfo.version.split("\\.")(0).toInt)
  }

  test("getDriverMinorVersion should return minor version") {
    assertEquals(connection.getMetaData.getDriverMinorVersion, BuildInfo.version.split("\\.")(1).toInt)
  }

  test("usesLocalFiles should return false") {
    assertEquals(connection.getMetaData.usesLocalFiles(), false)
  }

  test("usesLocalFilePerTable should return false") {
    assertEquals(connection.getMetaData.usesLocalFilePerTable(), false)
  }

  test("supportsMixedCaseIdentifiers should return false") {
    assertEquals(connection.getMetaData.supportsMixedCaseIdentifiers(), false)
  }

  test("storesUpperCaseIdentifiers should return false") {
    assertEquals(connection.getMetaData.storesUpperCaseIdentifiers(), false)
  }

  test("storesLowerCaseIdentifiers should return false") {
    assertEquals(connection.getMetaData.storesLowerCaseIdentifiers(), false)
  }

  test("storesMixedCaseIdentifiers should return false") {
    assertEquals(connection.getMetaData.storesMixedCaseIdentifiers(), false)
  }

  test("supportsMixedCaseQuotedIdentifiers should return false") {
    assertEquals(connection.getMetaData.supportsMixedCaseQuotedIdentifiers(), false)
  }

  test("storesUpperCaseQuotedIdentifiers should return false") {
    assertEquals(connection.getMetaData.storesUpperCaseQuotedIdentifiers(), false)
  }

  test("storesLowerCaseQuotedIdentifiers should return false") {
    assertEquals(connection.getMetaData.storesLowerCaseQuotedIdentifiers(), false)
  }

  test("storesMixedCaseQuotedIdentifiers should return false") {
    assertEquals(connection.getMetaData.storesMixedCaseQuotedIdentifiers(), false)
  }

  test("getIdentifierQuoteString should return null") {
    assertEquals(connection.getMetaData.getIdentifierQuoteString, null)
  }

  test("getSQLKeywords should return empty string") {
    assertEquals(connection.getMetaData.getSQLKeywords, "")
  }

  test("getNumericFunctions should return null") {
    assertEquals(connection.getMetaData.getNumericFunctions, null)
  }

  test("getStringFunctions should return null") {
    assertEquals(connection.getMetaData.getStringFunctions, null)
  }

  test("getSystemFunctions should return null") {
    assertEquals(connection.getMetaData.getSystemFunctions, null)
  }

  test("getTimeDateFunctions should return date") {
    assertEquals(connection.getMetaData.getTimeDateFunctions, "date")
  }

  test("getSearchStringEscape should return \\") {
    assertEquals(connection.getMetaData.getSearchStringEscape, "\\")
  }

  test("getExtraNameCharacters should return null") {
    assertEquals(connection.getMetaData.getExtraNameCharacters, null)
  }

  test("supportsAlterTableWithAddColumn should return false") {
    assertEquals(connection.getMetaData.supportsAlterTableWithAddColumn(), false)
  }

  test("supportsAlterTableWithDropColumn should return false") {
    assertEquals(connection.getMetaData.supportsAlterTableWithDropColumn(), false)
  }

  test("supportsColumnAliasing should return true") {
    assertEquals(connection.getMetaData.supportsColumnAliasing(), true)
  }

  test("nullPlusNonNullIsNull should return false") {
    assertEquals(connection.getMetaData.nullPlusNonNullIsNull(), false)
  }

  test("supportsConvert should return false") {
    assertEquals(connection.getMetaData.supportsConvert(), false)
  }

  test("supportsConvert with parameters should return false") {
    assertEquals(connection.getMetaData.supportsConvert(0, 0), false)
  }

  test("supportsTableCorrelationNames should return false") {
    assertEquals(connection.getMetaData.supportsTableCorrelationNames(), false)
  }

  test("supportsDifferentTableCorrelationNames should return false") {
    assertEquals(connection.getMetaData.supportsDifferentTableCorrelationNames(), false)
  }

  test("supportsExpressionsInOrderBy should return false") {
    assertEquals(connection.getMetaData.supportsExpressionsInOrderBy(), false)
  }

  test("supportsOrderByUnrelated should return true") {
    assertEquals(connection.getMetaData.supportsOrderByUnrelated(), true)
  }

  test("supportsGroupBy should return true") {
    assertEquals(connection.getMetaData.supportsGroupBy(), true)
  }

  test("supportsGroupByUnrelated should return true") {
    assertEquals(connection.getMetaData.supportsGroupByUnrelated(), true)
  }

  test("supportsGroupByBeyondSelect should return true") {
    assertEquals(connection.getMetaData.supportsGroupByBeyondSelect(), true)
  }

  test("supportsLikeEscapeClause should return true") {
    assertEquals(connection.getMetaData.supportsLikeEscapeClause(), true)
  }

  test("supportsMultipleResultSets should return true") {
    assertEquals(connection.getMetaData.supportsMultipleResultSets(), true)
  }

  test("supportsMultipleTransactions should return false") {
    assertEquals(connection.getMetaData.supportsMultipleTransactions(), false)
  }

  test("supportsNonNullableColumns should return true") {
    assertEquals(connection.getMetaData.supportsNonNullableColumns(), true)
  }

  test("supportsMinimumSQLGrammar should return false") {
    assertEquals(connection.getMetaData.supportsMinimumSQLGrammar(), false)
  }

  test("supportsCoreSQLGrammar should return false") {
    assertEquals(connection.getMetaData.supportsCoreSQLGrammar(), false)
  }

  test("supportsExtendedSQLGrammar should return false") {
    assertEquals(connection.getMetaData.supportsExtendedSQLGrammar(), false)
  }

  test("supportsANSI92EntryLevelSQL should return false") {
    assertEquals(connection.getMetaData.supportsANSI92EntryLevelSQL(), false)
  }

  test("supportsANSI92IntermediateSQL should return false") {
    assertEquals(connection.getMetaData.supportsANSI92IntermediateSQL(), false)
  }

  test("supportsANSI92FullSQL should return false") {
    assertEquals(connection.getMetaData.supportsANSI92FullSQL(), false)
  }

  test("supportsIntegrityEnhancementFacility should return false") {
    assertEquals(connection.getMetaData.supportsIntegrityEnhancementFacility(), false)
  }

  test("supportsOuterJoins should return false") {
    assertEquals(connection.getMetaData.supportsOuterJoins(), false)
  }

  test("supportsFullOuterJoins should return false") {
    assertEquals(connection.getMetaData.supportsFullOuterJoins(), false)
  }

  test("supportsLimitedOuterJoins should return false") {
    assertEquals(connection.getMetaData.supportsLimitedOuterJoins(), false)
  }

  test("getSchemaTerm should return database") {
    assertEquals(connection.getMetaData.getSchemaTerm, "database")
  }

  test("getProcedureTerm should return null") {
    assertEquals(connection.getMetaData.getProcedureTerm, null)
  }

  test("getCatalogTerm should return database") {
    assertEquals(connection.getMetaData.getCatalogTerm, "database")
  }

  test("isCatalogAtStart should return true") {
    assertEquals(connection.getMetaData.isCatalogAtStart, true)
  }

  test("getCatalogSeparator should return .") {
    assertEquals(connection.getMetaData.getCatalogSeparator, ".")
  }

  test("supportsSchemasInDataManipulation should return false") {
    assertEquals(connection.getMetaData.supportsSchemasInDataManipulation(), false)
  }

  test("supportsSchemasInProcedureCalls should return false") {
    assertEquals(connection.getMetaData.supportsSchemasInProcedureCalls(), false)
  }

  test("supportsSchemasInTableDefinitions should return false") {
    assertEquals(connection.getMetaData.supportsSchemasInTableDefinitions(), false)
  }

  test("supportsSchemasInIndexDefinitions should return false") {
    assertEquals(connection.getMetaData.supportsSchemasInIndexDefinitions(), false)
  }

  test("supportsSchemasInPrivilegeDefinitions should return false") {
    assertEquals(connection.getMetaData.supportsSchemasInPrivilegeDefinitions(), false)
  }

  test("supportsCatalogsInDataManipulation should return true") {
    assertEquals(connection.getMetaData.supportsCatalogsInDataManipulation(), true)
  }

  test("supportsCatalogsInProcedureCalls should return false") {
    assertEquals(connection.getMetaData.supportsCatalogsInProcedureCalls(), false)
  }

  test("supportsCatalogsInTableDefinitions should return false") {
    assertEquals(connection.getMetaData.supportsCatalogsInTableDefinitions(), false)
  }

  test("supportsCatalogsInIndexDefinitions should return false") {
    assertEquals(connection.getMetaData.supportsCatalogsInIndexDefinitions(), false)
  }

  test("supportsCatalogsInPrivilegeDefinitions should return false") {
    assertEquals(connection.getMetaData.supportsCatalogsInPrivilegeDefinitions(), false)
  }

  test("supportsPositionedDelete should return false") {
    assertEquals(connection.getMetaData.supportsPositionedDelete(), false)
  }

  test("supportsPositionedUpdate should return false") {
    assertEquals(connection.getMetaData.supportsPositionedUpdate(), false)
  }

  test("supportsSelectForUpdate should return false") {
    assertEquals(connection.getMetaData.supportsSelectForUpdate(), false)
  }

  test("supportsStoredProcedures should return false") {
    assertEquals(connection.getMetaData.supportsStoredProcedures(), false)
  }

  test("supportsSubqueriesInComparisons should return false") {
    assertEquals(connection.getMetaData.supportsSubqueriesInComparisons(), false)
  }

  test("supportsSubqueriesInExists should return false") {
    assertEquals(connection.getMetaData.supportsSubqueriesInExists(), false)
  }

  test("supportsSubqueriesInIns should return false") {
    assertEquals(connection.getMetaData.supportsSubqueriesInIns(), false)
  }

  test("supportsSubqueriesInQuantifieds should return false") {
    assertEquals(connection.getMetaData.supportsSubqueriesInQuantifieds(), false)
  }

  test("supportsCorrelatedSubqueries should return false") {
    assertEquals(connection.getMetaData.supportsCorrelatedSubqueries(), false)
  }

  test("supportsUnion should return true") {
    assertEquals(connection.getMetaData.supportsUnion(), true)
  }

  test("supportsUnionAll should return true") {
    assertEquals(connection.getMetaData.supportsUnionAll(), true)
  }

  test("supportsOpenCursorsAcrossCommit should return false") {
    assertEquals(connection.getMetaData.supportsOpenCursorsAcrossCommit(), false)
  }

  test("supportsOpenCursorsAcrossRollback should return false") {
    assertEquals(connection.getMetaData.supportsOpenCursorsAcrossRollback(), false)
  }

  test("supportsOpenStatementsAcrossCommit should return false") {
    assertEquals(connection.getMetaData.supportsOpenStatementsAcrossCommit(), false)
  }

  test("supportsOpenStatementsAcrossRollback should return false") {
    assertEquals(connection.getMetaData.supportsOpenStatementsAcrossRollback(), false)
  }

  test("getMaxBinaryLiteralLength should return 0") {
    assertEquals(connection.getMetaData.getMaxBinaryLiteralLength, 0)
  }

  test("getMaxCharLiteralLength should return 0") {
    assertEquals(connection.getMetaData.getMaxCharLiteralLength, 0)
  }

  test("getMaxColumnNameLength should return 0") {
    assertEquals(connection.getMetaData.getMaxColumnNameLength, 0)
  }

  test("getMaxColumnsInGroupBy should return 0") {
    assertEquals(connection.getMetaData.getMaxColumnsInGroupBy, 0)
  }

  test("getMaxColumnsInIndex should return 0") {
    assertEquals(connection.getMetaData.getMaxColumnsInIndex, 0)
  }

  test("getMaxColumnsInOrderBy should return 0") {
    assertEquals(connection.getMetaData.getMaxColumnsInOrderBy, 0)
  }

  test("getMaxColumnsInSelect should return 0") {
    assertEquals(connection.getMetaData.getMaxColumnsInSelect, 0)
  }

  test("getMaxColumnsInTable should return 0") {
    assertEquals(connection.getMetaData.getMaxColumnsInTable, 0)
  }

  test("getMaxConnections should return 0") {
    assertEquals(connection.getMetaData.getMaxConnections, 0)
  }

  test("getMaxCursorNameLength should return 0") {
    assertEquals(connection.getMetaData.getMaxCursorNameLength, 0)
  }

  test("getMaxIndexLength should return 0") {
    assertEquals(connection.getMetaData.getMaxIndexLength, 0)
  }

  test("getMaxSchemaNameLength should return 0") {
    assertEquals(connection.getMetaData.getMaxSchemaNameLength, 0)
  }

  test("getMaxProcedureNameLength should return 0") {
    assertEquals(connection.getMetaData.getMaxProcedureNameLength, 0)
  }

  test("getMaxCatalogNameLength should return 0") {
    assertEquals(connection.getMetaData.getMaxCatalogNameLength, 0)
  }

  test("getMaxRowSize should return 0") {
    assertEquals(connection.getMetaData.getMaxRowSize, 0)
  }

  test("doesMaxRowSizeIncludeBlobs should return false") {
    assertEquals(connection.getMetaData.doesMaxRowSizeIncludeBlobs(), false)
  }

  test("getMaxStatementLength should return 0") {
    assertEquals(connection.getMetaData.getMaxStatementLength, 0)
  }

  test("getMaxStatements should return 0") {
    assertEquals(connection.getMetaData.getMaxStatements, 0)
  }

  test("getMaxTableNameLength should return 90") {
    assertEquals(connection.getMetaData.getMaxTableNameLength, 90)
  }

  test("getMaxTablesInSelect should return 0") {
    assertEquals(connection.getMetaData.getMaxTablesInSelect, 0)
  }

  test("getMaxUserNameLength should return 0") {
    assertEquals(connection.getMetaData.getMaxUserNameLength, 0)
  }

  test("getDefaultTransactionIsolation should return TRANSACTION_NONE") {
    assertEquals(connection.getMetaData.getDefaultTransactionIsolation, Connection.TRANSACTION_NONE)
  }

  test("supportsTransactions should return false") {
    assertEquals(connection.getMetaData.supportsTransactions(), false)
  }

  test("supportsTransactionIsolationLevel should return false") {
    assertEquals(connection.getMetaData.supportsTransactionIsolationLevel(0), false)
  }

  test("supportsDataDefinitionAndDataManipulationTransactions should return false") {
    assertEquals(connection.getMetaData.supportsDataDefinitionAndDataManipulationTransactions(), false)
  }

  test("supportsDataManipulationTransactionsOnly should return false") {
    assertEquals(connection.getMetaData.supportsDataManipulationTransactionsOnly(), false)
  }

  test("dataDefinitionCausesTransactionCommit should return false") {
    assertEquals(connection.getMetaData.dataDefinitionCausesTransactionCommit(), false)
  }

  test("dataDefinitionIgnoredInTransactions should return false") {
    assertEquals(connection.getMetaData.dataDefinitionIgnoredInTransactions(), false)
  }

  test("getProcedures should return empty ResultSet") {
    val resultSet = connection.getMetaData.getProcedures("", "", "")
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), false)
  }

  test("getProcedureColumns should return empty ResultSet") {
    val resultSet = connection.getMetaData.getProcedureColumns("", "", "", "")
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), false)
  }

  test("getTables should return ResultSet with tables") {
    val resultSet = connection.getMetaData.getTables("", "", "", Array("TABLE"))
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), true)
    assertEquals(resultSet.getString("TABLE_NAME"), "system.version")
  }

  test("getSchemas should return ResultSet with schemas") {
    val resultSet = connection.getMetaData.getSchemas
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), true)
    assertEquals(resultSet.getString("TABLE_SCHEM"), "admin")
  }

  test("getCatalogs should return ResultSet with catalogs") {
    val resultSet = connection.getMetaData.getCatalogs
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), true)
    assertEquals(resultSet.getString("TABLE_CAT"), "mongodb")
  }

  test("getTableTypes should return ResultSet with table types") {
    val resultSet = connection.getMetaData.getTableTypes
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), true)
    assertEquals(resultSet.getString("TABLE_TYPE"), "COLLECTION")
  }

  test("getColumns should return ResultSet with columns") {
    val resultSet = connection.getMetaData.getColumns("", "db1", "coll1", "")
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), false)
  }

  test("getColumnPrivileges should return null") {
    assertEquals(connection.getMetaData.getColumnPrivileges("", "", "", ""), null)
  }

  test("getTablePrivileges should return null") {
    assertEquals(connection.getMetaData.getTablePrivileges("", "", ""), null)
  }

  test("getBestRowIdentifier should return null") {
    assertEquals(connection.getMetaData.getBestRowIdentifier("", "", "", 0, false), null)
  }

  test("getVersionColumns should return null") {
    assertEquals(connection.getMetaData.getVersionColumns("", "", ""), null)
  }

  test("getPrimaryKeys should return ResultSet with primary keys") {
    val resultSet = connection.getMetaData.getPrimaryKeys("", "testSchema", "testTable")
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    // Add more assertions based on the expected content of the ResultSet
  }

  test("getImportedKeys should return null") {
    assertEquals(connection.getMetaData.getImportedKeys("", "", ""), null)
  }

  test("getExportedKeys should return null") {
    assertEquals(connection.getMetaData.getExportedKeys("", "", ""), null)
  }

  test("getCrossReference should return null") {
    assertEquals(connection.getMetaData.getCrossReference("", "", "", "", "", ""), null)
  }

  test("getTypeInfo should return ResultSet with type info") {
    val resultSet = connection.getMetaData.getTypeInfo
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    // Add more assertions based on the expected content of the ResultSet
  }

  test("getIndexInfo should return ResultSet with index info") {
    val resultSet = connection.getMetaData.getIndexInfo("", "testSchema", "testTable", false, false)
    assert(resultSet.isInstanceOf[MongoDbResultSet])
  }

  test("supportsResultSetType should return true for TYPE_FORWARD_ONLY") {
    assertEquals(connection.getMetaData.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY), true)
  }

  test("supportsResultSetConcurrency should return false") {
    assertEquals(connection.getMetaData.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), false)
  }

  test("ownUpdatesAreVisible should return false") {
    assertEquals(connection.getMetaData.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("ownDeletesAreVisible should return false") {
    assertEquals(connection.getMetaData.ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("ownInsertsAreVisible should return false") {
    assertEquals(connection.getMetaData.ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("othersUpdatesAreVisible should return false") {
    assertEquals(connection.getMetaData.othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("othersDeletesAreVisible should return false") {
    assertEquals(connection.getMetaData.othersDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("othersInsertsAreVisible should return false") {
    assertEquals(connection.getMetaData.othersInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("updatesAreDetected should return false") {
    assertEquals(connection.getMetaData.updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("deletesAreDetected should return false") {
    assertEquals(connection.getMetaData.deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("insertsAreDetected should return false") {
    assertEquals(connection.getMetaData.insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY), false)
  }

  test("supportsBatchUpdates should return false") {
    assertEquals(connection.getMetaData.supportsBatchUpdates(), false)
  }

  test("getUDTs should return empty ResultSet") {
    val resultSet = connection.getMetaData.getUDTs("", "", "", Array())
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), false)
  }

  test("getConnection should return the connection") {
    assertEquals(connection.getMetaData.getConnection, connection)
  }

  test("supportsSavepoints should return false") {
    assertEquals(connection.getMetaData.supportsSavepoints(), false)
  }

  test("supportsNamedParameters should return false") {
    assertEquals(connection.getMetaData.supportsNamedParameters(), false)
  }

  test("supportsMultipleOpenResults should return false") {
    assertEquals(connection.getMetaData.supportsMultipleOpenResults(), false)
  }

  test("supportsGetGeneratedKeys should return false") {
    assertEquals(connection.getMetaData.supportsGetGeneratedKeys(), false)
  }

  test("getSuperTypes should return empty ResultSet") {
    val resultSet = connection.getMetaData.getSuperTypes("", "", "")
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), false)
  }

  test("getSuperTables should return empty ResultSet") {
    val resultSet = connection.getMetaData.getSuperTables("", "", "")
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), false)
  }

  test("getAttributes should return empty ResultSet") {
    val resultSet = connection.getMetaData.getAttributes("", "", "", "")
    assert(resultSet.isInstanceOf[MongoDbResultSet])
    assertEquals(resultSet.next(), false)
  }

  test("supportsResultSetHoldability should return false") {
    assertEquals(connection.getMetaData.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT), false)
  }

  test("getResultSetHoldability should return HOLD_CURSORS_OVER_COMMIT") {
    assertEquals(connection.getMetaData.getResultSetHoldability, ResultSet.HOLD_CURSORS_OVER_COMMIT)
  }

  test("getDatabaseMajorVersion should return the major version") {
    assertEquals(connection.getMetaData.getDatabaseMajorVersion, 3) // Replace with the actual major version
  }

  test("getDatabaseMinorVersion should return the minor version") {
    assertEquals(connection.getMetaData.getDatabaseMinorVersion, 0) // Replace with the actual minor version
  }

  test("getJDBCMajorVersion should return the JDBC major version") {
    assertEquals(connection.getMetaData.getJDBCMajorVersion, 4)
  }

  test("getJDBCMinorVersion should return the JDBC minor version") {
    assertEquals(connection.getMetaData.getJDBCMinorVersion, 2)
  }

  test("getSQLStateType should return sqlStateXOpen") {
    assertEquals(connection.getMetaData.getSQLStateType, DatabaseMetaData.sqlStateXOpen)
  }

  test("locatorsUpdateCopy should return false") {
    assertEquals(connection.getMetaData.locatorsUpdateCopy(), false)
  }

  test("supportsStatementPooling should return false") {
    assertEquals(connection.getMetaData.supportsStatementPooling(), false)
  }

  test("getRowIdLifetime should return null") {
    assertEquals(connection.getMetaData.getRowIdLifetime, null)
  }
}
