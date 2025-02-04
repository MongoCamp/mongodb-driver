package dev.mongocamp.driver.mongodb.jdbc

import com.vdurmont.semver4j.Semver
import dev.mongocamp.driver.mongodb.database.DatabaseProvider.CollectionSeparator
import dev.mongocamp.driver.mongodb.jdbc.resultSet.MongoDbResultSet
import dev.mongocamp.driver.mongodb.schema.SchemaExplorer
import dev.mongocamp.driver.mongodb.{ BuildInfo, Converter, GenericObservable }
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.{ BsonNull, BsonString }
import org.mongodb.scala.documentToUntypedDocument

import java.sql.{ Connection, DatabaseMetaData, ResultSet, RowIdLifetime, Types }
import scala.collection.mutable.ArrayBuffer

class MongoDatabaseMetaData(connection: MongoJdbcConnection) extends DatabaseMetaData {
  private lazy val semVer          = new Semver(BuildInfo.version)
  private lazy val jdbcSemVer      = new Semver("4.2.0")
  private lazy val DatabaseNameKey = "mongodb"

  override def allProceduresAreCallable() = false

  override def allTablesAreSelectable(): Boolean = false

  override def getURL: String = {
    connection.getDatabaseProvider.connectionString
  }

  override def getUserName: String = connection.getDatabaseProvider.config.userName.getOrElse("not set")

  override def isReadOnly: Boolean = false

  override def nullsAreSortedHigh(): Boolean = false

  override def nullsAreSortedLow(): Boolean = false

  override def nullsAreSortedAtStart(): Boolean = false

  override def nullsAreSortedAtEnd(): Boolean = false

  override def getDatabaseProductName: String = DatabaseNameKey

  override def getDatabaseProductVersion: String = {
    connection.getDatabaseProvider.runCommand(Document("buildInfo" -> 1)).map(doc => doc.getString("version")).result(10)
  }

  override def getDriverName: String = BuildInfo.name

  override def getDriverVersion: String = semVer.getValue

  override def getDriverMajorVersion: Int = semVer.getMajor

  override def getDriverMinorVersion: Int = semVer.getMinor

  override def usesLocalFiles(): Boolean = false

  override def usesLocalFilePerTable(): Boolean = false

  override def supportsMixedCaseIdentifiers(): Boolean = false

  override def storesUpperCaseIdentifiers(): Boolean = false

  override def storesLowerCaseIdentifiers(): Boolean = false

  override def storesMixedCaseIdentifiers(): Boolean = false

  override def supportsMixedCaseQuotedIdentifiers(): Boolean = false

  override def storesUpperCaseQuotedIdentifiers(): Boolean = false

  override def storesLowerCaseQuotedIdentifiers(): Boolean = false

  override def storesMixedCaseQuotedIdentifiers(): Boolean = false

  override def getIdentifierQuoteString: String = null

  override def getSQLKeywords: String = ""

  override def getNumericFunctions: String = null

  override def getStringFunctions: String = null

  override def getSystemFunctions: String = null

  override def getTimeDateFunctions: String = "date"

  override def getSearchStringEscape: String = "\\"

  override def getExtraNameCharacters: String = null

  override def supportsAlterTableWithAddColumn(): Boolean = false

  override def supportsAlterTableWithDropColumn(): Boolean = false

  override def supportsColumnAliasing(): Boolean = true

  override def nullPlusNonNullIsNull(): Boolean = false

  override def supportsConvert(): Boolean = false

  override def supportsConvert(fromType: Int, toType: Int): Boolean = false

  override def supportsTableCorrelationNames(): Boolean = false

  override def supportsDifferentTableCorrelationNames(): Boolean = false

  override def supportsExpressionsInOrderBy(): Boolean = false

  override def supportsOrderByUnrelated(): Boolean = true

  override def supportsGroupBy(): Boolean = true

  override def supportsGroupByUnrelated(): Boolean = true

  override def supportsGroupByBeyondSelect(): Boolean = true

  override def supportsLikeEscapeClause(): Boolean = true

  override def supportsMultipleResultSets(): Boolean = true

  override def supportsMultipleTransactions(): Boolean = false

  override def supportsNonNullableColumns(): Boolean = true

  override def supportsMinimumSQLGrammar(): Boolean = false

  override def supportsCoreSQLGrammar(): Boolean = false

  override def supportsExtendedSQLGrammar(): Boolean = false

  override def supportsANSI92EntryLevelSQL(): Boolean = false

  override def supportsANSI92IntermediateSQL(): Boolean = false

  override def supportsANSI92FullSQL(): Boolean = false

  override def supportsIntegrityEnhancementFacility(): Boolean = false

  override def supportsOuterJoins(): Boolean = false

  override def supportsFullOuterJoins(): Boolean = false

  override def supportsLimitedOuterJoins(): Boolean = false

  override def getSchemaTerm: String = "database"

  override def getProcedureTerm: String = null

  override def getCatalogTerm: String = "database"

  override def isCatalogAtStart: Boolean = true

  override def getCatalogSeparator: String = "."

  override def supportsSchemasInDataManipulation(): Boolean = false

  override def supportsSchemasInProcedureCalls(): Boolean = false

  override def supportsSchemasInTableDefinitions(): Boolean = false

  override def supportsSchemasInIndexDefinitions(): Boolean = false

  override def supportsSchemasInPrivilegeDefinitions(): Boolean = false

  override def supportsCatalogsInDataManipulation(): Boolean = true

  override def supportsCatalogsInProcedureCalls(): Boolean = false

  override def supportsCatalogsInTableDefinitions(): Boolean = false

  override def supportsCatalogsInIndexDefinitions(): Boolean = false

  override def supportsCatalogsInPrivilegeDefinitions(): Boolean = false

  override def supportsPositionedDelete(): Boolean = false

  override def supportsPositionedUpdate(): Boolean = false

  override def supportsSelectForUpdate(): Boolean = false

  override def supportsStoredProcedures(): Boolean = false

  override def supportsSubqueriesInComparisons(): Boolean = false

  override def supportsSubqueriesInExists(): Boolean = false

  override def supportsSubqueriesInIns(): Boolean = false

  override def supportsSubqueriesInQuantifieds(): Boolean = false

  override def supportsCorrelatedSubqueries(): Boolean = false

  override def supportsUnion(): Boolean = true

  override def supportsUnionAll(): Boolean = true

  override def supportsOpenCursorsAcrossCommit(): Boolean = false

  override def supportsOpenCursorsAcrossRollback(): Boolean = false

  override def supportsOpenStatementsAcrossCommit(): Boolean = false

  override def supportsOpenStatementsAcrossRollback(): Boolean = false

  override def getMaxBinaryLiteralLength: Int = 0

  override def getMaxCharLiteralLength: Int = 0

  override def getMaxColumnNameLength: Int = 0

  override def getMaxColumnsInGroupBy: Int = 0

  override def getMaxColumnsInIndex: Int = 0

  override def getMaxColumnsInOrderBy: Int = 0

  override def getMaxColumnsInSelect: Int = 0

  override def getMaxColumnsInTable: Int = 0

  override def getMaxConnections: Int = 0

  override def getMaxCursorNameLength: Int = 0

  override def getMaxIndexLength: Int = 0

  override def getMaxSchemaNameLength: Int = 0

  override def getMaxProcedureNameLength: Int = 0

  override def getMaxCatalogNameLength: Int = 0

  override def getMaxRowSize: Int = 0

  override def doesMaxRowSizeIncludeBlobs(): Boolean = false

  override def getMaxStatementLength: Int = 0

  override def getMaxStatements: Int = 0

  override def getMaxTableNameLength: Int = 90

  override def getMaxTablesInSelect: Int = 0

  override def getMaxUserNameLength: Int = 0

  override def getDefaultTransactionIsolation: Int = Connection.TRANSACTION_NONE

  override def supportsTransactions(): Boolean = false

  override def supportsTransactionIsolationLevel(level: Int): Boolean = false

  override def supportsDataDefinitionAndDataManipulationTransactions(): Boolean = false

  override def supportsDataManipulationTransactionsOnly(): Boolean = false

  override def dataDefinitionCausesTransactionCommit(): Boolean = false

  override def dataDefinitionIgnoredInTransactions(): Boolean = false

  override def getProcedures(catalog: String, schemaPattern: String, procedureNamePattern: String): ResultSet = { new MongoDbResultSet(null, List.empty, 10) }

  override def getProcedureColumns(catalog: String, schemaPattern: String, procedureNamePattern: String, columnNamePattern: String): ResultSet = {
    new MongoDbResultSet(null, List.empty, 10)
  }

  override def getTables(catalog: String, schemaPattern: String, tableNamePattern: String, types: Array[String]): ResultSet = {
    val internalSchemaPattern    = Option(schemaPattern).getOrElse("(.*?)")
    val internalTableNamePattern = Option(tableNamePattern).getOrElse("(.*?)")
    val documents: List[Document] = connection.getDatabaseProvider.databaseNames
      .filter(s => internalSchemaPattern.r.findFirstMatchIn(s).nonEmpty)
      .flatMap(dbName => {
        val collDocuments: List[Document] = connection.getDatabaseProvider
          .collectionNames(dbName)
          .filter(s => internalTableNamePattern.r.findFirstMatchIn(s).nonEmpty)
          .map(collName => {
            Document(
              "TABLE_CAT"                 -> BsonString(DatabaseNameKey),
              "TABLE_SCHEM"               -> BsonString(dbName),
              "TABLE_NAME"                -> BsonString(collName),
              "TABLE_TYPE"                -> BsonString("TABLE"),
              "REMARKS"                   -> BsonString("COLLECTION"),
              "TYPE_CAT"                  -> BsonString(DatabaseNameKey),
              "TYPE_SCHEM"                -> BsonString(dbName),
              "TYPE_NAME"                 -> BsonString("COLLECTION"),
              "SELF_REFERENCING_COL_NAME" -> BsonNull(),
              "REF_GENERATION"            -> BsonNull()
            )
          })
        collDocuments
      })
    new MongoDbResultSet(null, documents, 10)
  }

  override def getSchemas: ResultSet = getSchemas("", "(.*?)")

  override def getCatalogs: ResultSet = {
    val documents = List(
      Document(
        "TABLE_CAT" -> DatabaseNameKey
      )
    )
    new MongoDbResultSet(null, documents, 10)
  }

  override def getTableTypes: ResultSet = {
    val documents = List(
      Document(
        "TABLE_TYPE" -> "COLLECTION"
      )
    )
    new MongoDbResultSet(null, documents, 10)
  }

  override def getColumns(catalog: String, schemaPattern: String, tableNamePattern: String, columnNamePattern: String): ResultSet = {
    val schemaRegex     = schemaPattern.replace("%", "(.*?)").r
    val tableNameRegex  = tableNamePattern.replace("%", "(.*?)").r
    val columnNameRegex = columnNamePattern.replace("%", "(.*?)").r
    val databaseNames   = connection.getDatabaseProvider.databaseNames.filter(s => schemaRegex.findFirstMatchIn(s).nonEmpty)
    val documents       = ArrayBuffer[Document]()
    val schemaExplorer  = new SchemaExplorer()
    var i               = 0
    databaseNames.map(dbName => {
      val allCollections = connection.getDatabaseProvider.collectionNames(dbName)
      val filtered       = allCollections.filter(tbl => tableNameRegex.findFirstMatchIn(tbl).nonEmpty)
      filtered.map(table => {
        val dao             = connection.getDatabaseProvider.dao(s"$dbName$CollectionSeparator$table")
        val schemaAnalysis  = schemaExplorer.analyzeSchema(dao)
        val relevantColumns = schemaAnalysis.fields.filter(field => columnNameRegex.findFirstMatchIn(field.name).nonEmpty)
        relevantColumns.foreach(schemaAnalysis => {
          val fieldTypeName              = schemaAnalysis.fieldTypes.head.fieldType
          var decimalDigits: Option[Int] = None
          val fieldType = fieldTypeName match {
            case "string"   => Types.LONGVARCHAR
            case "null"     => Types.VARCHAR
            case "objectId" => Types.VARCHAR
            case "date"     => Types.DATE
            case "int" =>
              decimalDigits = Some(0)
              Types.INTEGER
            case "long" =>
              decimalDigits = Some(0)
              Types.BIGINT
            case "number" =>
              decimalDigits = Some(Int.MaxValue)
              Types.DOUBLE
            case "double" =>
              decimalDigits = Some(Int.MaxValue)
              Types.DOUBLE
            case "array"  => Types.ARRAY
            case "bool"   => Types.BOOLEAN
            case "object" => Types.JAVA_OBJECT
            case _ =>
              Types.VARCHAR
          }
          documents += Converter.toDocument(
            Map(
              "TABLE_CAT"         -> DatabaseNameKey,
              "TABLE_SCHEM"       -> dbName,
              "TABLE_NAME"        -> table,
              "COLUMN_NAME"       -> schemaAnalysis.name,
              "DATA_TYPE"         -> fieldType,
              "TYPE_NAME"         -> fieldTypeName,
              "COLUMN_SIZE"       -> null,
              "BUFFER_LENGTH"     -> null,
              "DECIMAL_DIGITS"    -> decimalDigits.getOrElse(null),
              "NUM_PREC_RADIX"    -> null,
              "NULLABLE"          -> DatabaseMetaData.columnNullable, // how to check
              "REMARKS"           -> null,
              "COLUMN_DEF"        -> null,
              "SQL_DATA_TYPE"     -> null,
              "SQL_DATETIME_SUB"  -> null,
              "CHAR_OCTET_LENGTH" -> null,
              "ORDINAL_POSITION"  -> i,
              "IS_NULLABLE"       -> "YES",
              "SCOPE_CATLOG"      -> null,
              "SCOPE_SCHEMA"      -> null,
              "SCOPE_TABLE"       -> null,
              "SOURCE_DATA_TYPE"  -> null,
              "IS_AUTOINCREMENT"  -> "NO"
            )
          )
          i = i + 1
        })
      })
    })
    new MongoDbResultSet(null, documents.toList, 10)
  }

  override def getColumnPrivileges(catalog: String, schema: String, table: String, columnNamePattern: String): ResultSet = {
    null
  }

  override def getTablePrivileges(catalog: String, schemaPattern: String, tableNamePattern: String): ResultSet = {
    null
  }

  override def getBestRowIdentifier(catalog: String, schema: String, table: String, scope: Int, nullable: Boolean): ResultSet = {
    null
  }

  override def getVersionColumns(catalog: String, schema: String, table: String): ResultSet = {
    null
  }

  override def getPrimaryKeys(catalog: String, schema: String, table: String): ResultSet = {
    val dao           = connection.getDatabaseProvider.dao(s"$schema$CollectionSeparator$table")
    val uniqueIndices = dao.indexList().filter(_.unique)
    val pkDocuments = uniqueIndices.map(i =>
      Map(
        "TABLE_CAT"   -> DatabaseNameKey,
        "TABLE_SCHEM" -> schema,
        "TABLE_NAME"  -> table,
        "COLUMN_NAME" -> i.fields.head,
        "KEY_SEQ"     -> 0,
        "PK_NAME"     -> i.name
      )
    )
    new MongoDbResultSet(null, pkDocuments.map(i => Converter.toDocument(i)), 10)

  }

  override def getImportedKeys(catalog: String, schema: String, table: String): ResultSet = {
    null
  }

  override def getExportedKeys(catalog: String, schema: String, table: String): ResultSet = {
    null
  }

  override def getCrossReference(
      parentCatalog: String,
      parentSchema: String,
      parentTable: String,
      foreignCatalog: String,
      foreignSchema: String,
      foreignTable: String
  ): ResultSet = {
    null
  }

  override def getTypeInfo: ResultSet = {
    val objectIdValue = "OBJECT_ID"
    val documentValue = "DOCUMENT"
    val types = List(
      Map(
        "TYPE_NAME"          -> objectIdValue,
        "DATA_TYPE"          -> Types.VARCHAR,
        "PRECISION"          -> "800",
        "LITERAL_PREFIX"     -> "'",
        "LITERAL_SUFFIX"     -> "'",
        "CREATE_PARAMS"      -> null,
        "NULLABLE"           -> DatabaseMetaData.typeNullable,
        "CASE_SENSITIVE"     -> true,
        "SEARCHABLE"         -> DatabaseMetaData.typeSearchable,
        "UNSIGNED_ATTRIBUTE" -> false,
        "FIXED_PREC_SCALE"   -> false,
        "AUTO_INCREMENT"     -> false,
        "LOCAL_TYPE_NAME"    -> objectIdValue,
        "MINIMUM_SCALE"      -> 0,
        "MAXIMUM_SCALE"      -> 0,
        "SQL_DATA_TYPE"      -> null,
        "SQL_DATETIME_SUB"   -> null,
        "NUM_PREC_RADIX"     -> 10
      ),
      Map(
        "TYPE_NAME"          -> documentValue,
        "DATA_TYPE"          -> Types.CLOB,
        "PRECISION"          -> "16777216",
        "LITERAL_PREFIX"     -> "'",
        "LITERAL_SUFFIX"     -> "'",
        "CREATE_PARAMS"      -> null,
        "NULLABLE"           -> DatabaseMetaData.typeNullable,
        "CASE_SENSITIVE"     -> true,
        "SEARCHABLE"         -> DatabaseMetaData.typeSearchable,
        "UNSIGNED_ATTRIBUTE" -> false,
        "FIXED_PREC_SCALE"   -> false,
        "AUTO_INCREMENT"     -> false,
        "LOCAL_TYPE_NAME"    -> documentValue,
        "MINIMUM_SCALE"      -> 0,
        "MAXIMUM_SCALE"      -> 0,
        "SQL_DATA_TYPE"      -> null,
        "SQL_DATETIME_SUB"   -> null,
        "NUM_PREC_RADIX"     -> 10
      )
    )
    new MongoDbResultSet(null, types.map(i => Converter.toDocument(i)), 10)
  }

  override def getIndexInfo(catalog: String, schema: String, table: String, unique: Boolean, approximate: Boolean): ResultSet = {
    val schemaRegex    = schema.r
    val tableNameRegex = table.r
    val databaseNames  = connection.getDatabaseProvider.databaseNames.filter(s => schemaRegex.findFirstMatchIn(s).nonEmpty)
    val documents      = ArrayBuffer[Document]()
    databaseNames.map(dbName => {
      val allCollections = connection.getDatabaseProvider.collectionNames(dbName)
      allCollections
        .filter(tbl => tableNameRegex.findFirstMatchIn(tbl).nonEmpty)
        .map(table => {
          val dao = connection.getDatabaseProvider.dao(s"$dbName$CollectionSeparator$table")
          dao
            .indexList()
            .map(index => {
              val fields = index.fields
              fields.zipWithIndex.foreach { case (field, i) =>
                documents += Converter.toDocument(
                  Map(
                    "TABLE_CAT"        -> DatabaseNameKey,
                    "TABLE_SCHEM"      -> dbName,
                    "TABLE_NAME"       -> table,
                    "NON_UNIQUE"       -> (if (!index.unique) "YES" else "NO"),
                    "INDEX_QUALIFIER"  -> dbName,
                    "INDEX_NAME"       -> index.name,
                    "TYPE"             -> 0,
                    "ORDINAL_POSITION" -> i,
                    "COLUMN_NAME"      -> field,
                    "ASC_OR_DESC"      -> "A",
                    "CARDINALITY"      -> "0",
                    "PAGES"            -> "0",
                    "FILTER_CONDITION" -> ""
                  )
                )
              }
            })
        })
    })
    new MongoDbResultSet(null, documents.toList, 10)
  }

  override def supportsResultSetType(`type`: Int): Boolean = {
    `type` == ResultSet.TYPE_FORWARD_ONLY
  }

  override def supportsResultSetConcurrency(`type`: Int, concurrency: Int): Boolean = false

  override def ownUpdatesAreVisible(`type`: Int): Boolean = false

  override def ownDeletesAreVisible(`type`: Int): Boolean = false

  override def ownInsertsAreVisible(`type`: Int): Boolean = false

  override def othersUpdatesAreVisible(`type`: Int): Boolean = false

  override def othersDeletesAreVisible(`type`: Int): Boolean = false

  override def othersInsertsAreVisible(`type`: Int): Boolean = false

  override def updatesAreDetected(`type`: Int): Boolean = false

  override def deletesAreDetected(`type`: Int): Boolean = false

  override def insertsAreDetected(`type`: Int): Boolean = false

  override def supportsBatchUpdates(): Boolean = false

  override def getUDTs(catalog: String, schemaPattern: String, typeNamePattern: String, types: Array[Int]): ResultSet = {
    new MongoDbResultSet(null, List.empty, 10)
  }

  override def getConnection: Connection = connection

  override def supportsSavepoints(): Boolean = false

  override def supportsNamedParameters(): Boolean = false

  override def supportsMultipleOpenResults(): Boolean = false

  override def supportsGetGeneratedKeys(): Boolean = false

  override def getSuperTypes(catalog: String, schemaPattern: String, typeNamePattern: String): ResultSet = { new MongoDbResultSet(null, List.empty, 10) }

  override def getSuperTables(catalog: String, schemaPattern: String, tableNamePattern: String): ResultSet = { new MongoDbResultSet(null, List.empty, 10) }

  override def getAttributes(catalog: String, schemaPattern: String, typeNamePattern: String, attributeNamePattern: String): ResultSet = {
    new MongoDbResultSet(null, List.empty, 10)
  }

  override def supportsResultSetHoldability(holdability: Int): Boolean = false

  override def getResultSetHoldability: Int = ResultSet.HOLD_CURSORS_OVER_COMMIT

  override def getDatabaseMajorVersion: Int = semVer.getMajor

  override def getDatabaseMinorVersion: Int = semVer.getMinor

  override def getJDBCMajorVersion: Int = jdbcSemVer.getMajor

  override def getJDBCMinorVersion: Int = jdbcSemVer.getMinor

  override def getSQLStateType: Int = DatabaseMetaData.sqlStateXOpen

  override def locatorsUpdateCopy(): Boolean = false

  override def supportsStatementPooling(): Boolean = false

  override def getRowIdLifetime: RowIdLifetime = null

  override def getSchemas(catalog: String, schemaPattern: String): ResultSet = {
    val documents = connection.getDatabaseProvider.databaseNames
      .filter(s => schemaPattern.r.findFirstMatchIn(s).nonEmpty)
      .map(dbName => {
        Document(
          "TABLE_SCHEM"   -> dbName,
          "TABLE_CATALOG" -> DatabaseNameKey
        )
      })
    new MongoDbResultSet(null, documents, 10)
  }

  override def supportsStoredFunctionsUsingCallSyntax(): Boolean = false

  override def autoCommitFailureClosesAllResultSets(): Boolean = false

  override def getClientInfoProperties: ResultSet = { new MongoDbResultSet(null, List.empty, 10) }

  override def getFunctions(catalog: String, schemaPattern: String, functionNamePattern: String): ResultSet = { new MongoDbResultSet(null, List.empty, 10) }

  override def getFunctionColumns(catalog: String, schemaPattern: String, functionNamePattern: String, columnNamePattern: String): ResultSet = {
    new MongoDbResultSet(null, List.empty, 10)
  }

  override def getPseudoColumns(catalog: String, schemaPattern: String, tableNamePattern: String, columnNamePattern: String): ResultSet = {
    new MongoDbResultSet(null, List.empty, 10)
  }

  override def generatedKeyAlwaysReturned(): Boolean = false

  override def unwrap[T](iface: Class[T]): T = null.asInstanceOf[T]

  override def isWrapperFor(iface: Class[_]): Boolean = false
}
