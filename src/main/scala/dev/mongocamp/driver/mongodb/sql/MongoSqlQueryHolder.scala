package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import SQLCommandType.SQLCommandType
import com.mongodb.client.model.DropIndexOptions
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.expression.operators.conditional.{ AndExpression, OrExpression }
import net.sf.jsqlparser.expression.operators.relational._
import net.sf.jsqlparser.expression.{ Expression, Parenthesis }
import net.sf.jsqlparser.parser.{ CCJSqlParser, StreamProvider }
import net.sf.jsqlparser.schema.{ Column, Table }
import net.sf.jsqlparser.statement.create.index.CreateIndex
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.drop.Drop
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.{ AllColumns, FromItem, PlainSelect, Select, SelectItem }
import net.sf.jsqlparser.statement.truncate.Truncate
import net.sf.jsqlparser.statement.update.Update
import org.bson.conversions.Bson
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.model.Sorts.ascending
import org.mongodb.scala.{ Document, Observable }

import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

class MongoSqlQueryHolder {
  private val aggregatePipeline: ArrayBuffer[Document]       = ArrayBuffer()
  private var sqlTable: Table                                = _
  private var alias: Option[String]                          = None
  private var sqlCommandType: SQLCommandType                 = _
  private var updateOrDeleteFilter: Option[Map[String, Any]] = None
  private var setElement: Option[Bson]                       = None
  private val documentsToInsert: ArrayBuffer[Document]       = ArrayBuffer.empty
  private var indexOptions: Option[IndexOptions]             = None

  def this(statement: net.sf.jsqlparser.statement.Statement) = {
    this()

    if (classOf[Select].isAssignableFrom(statement.getClass)) {
      val select = statement.asInstanceOf[Select]
      convertSelectStatement(select)
    }
    else if (classOf[Insert].isAssignableFrom(statement.getClass)) {
      val insert = statement.asInstanceOf[Insert]
      convertInsertStatement(insert)
    }
    else if (classOf[Update].isAssignableFrom(statement.getClass)) {
      val update = statement.asInstanceOf[Update]
      convertUpdateStatement(update)
    }
    else if (classOf[Delete].isAssignableFrom(statement.getClass)) {
      val delete = statement.asInstanceOf[Delete]
      convertDeleteStatement(delete)
    }
    else if (classOf[CreateIndex].isAssignableFrom(statement.getClass)) {
      val createIndex = statement.asInstanceOf[CreateIndex]
      convertCreateIndexStatement(createIndex)
    }
    else if (classOf[Drop].isAssignableFrom(statement.getClass)) {
      val drop = statement.asInstanceOf[Drop]
      drop.getType.toUpperCase match {
        case "TABLE" =>
          sqlCommandType = SQLCommandType.DropTable
          sqlTable = drop.getName
        case "INDEX" =>
          sqlCommandType = SQLCommandType.DropIndex
          sqlTable = drop.getName
          if (!getCollection.contains(".")) {
            throw new IllegalArgumentException("not supported drop index without collection specified in the name")
          }
        case "DATABASE" =>
          sqlCommandType = SQLCommandType.DropDatabase
          sqlTable = drop.getName
        case _ =>
          throw new IllegalArgumentException("not supported drop command type")
      }
    }
    else if (classOf[Truncate].isAssignableFrom(statement.getClass)) {
      val truncate = statement.asInstanceOf[Truncate]
      sqlCommandType = SQLCommandType.Delete
      sqlTable = truncate.getTable
    }
    else {
      throw new IllegalArgumentException("not supported sql command type")
    }
    ""
  }

  def getCollection: String = sqlTable.getFullyQualifiedName

  def run(provider: DatabaseProvider, allowDiskUsage: Boolean = true): Observable[Document] = {
    sqlCommandType match {
      case SQLCommandType.Insert =>
        provider
          .dao(getCollection)
          .insertMany(documentsToInsert.toList)
          .map(e => {
            val map      = e.getInsertedIds.asScala.map(d => d._1.toString -> d._2).toMap
            val document = org.mongodb.scala.Document("wasAcknowledged" -> e.wasAcknowledged(), "insertedIds" -> Document(map))
            document
          })

      case SQLCommandType.Select =>
        provider.dao(getCollection).findAggregated(aggregatePipeline.toList, allowDiskUsage)

      case SQLCommandType.Update =>
        val updateSet = setElement.getOrElse(throw new IllegalArgumentException("update set element must be defined"))
        provider
          .dao(getCollection)
          .updateMany(getUpdateOrDeleteFilter, Map("$set" -> updateSet))
          .map(e =>
            org.mongodb.scala.Document(
              "modifiedCount"   -> e.getModifiedCount,
              "matchedCount"    -> e.getMatchedCount,
              "wasAcknowledged" -> e.wasAcknowledged()
            )
          )

      case SQLCommandType.Delete =>
        provider
          .dao(getCollection)
          .deleteMany(getUpdateOrDeleteFilter)
          .map(e => org.mongodb.scala.Document("deletedCount" -> e.getDeletedCount, "wasAcknowledged" -> e.wasAcknowledged()))

      case SQLCommandType.CreateIndex =>
        provider.dao(getCollection).createIndex(setElement.get, indexOptions.get).map(e => org.mongodb.scala.Document("indexName" -> e))

      case SQLCommandType.DropIndex =>
        val collectionName = sqlTable.getSchemaName
        val indexName      = sqlTable.getName
        provider
          .dao(collectionName)
          .dropIndexForName(indexName, new DropIndexOptions().maxTime(1, TimeUnit.MINUTES))
          .map(_ => org.mongodb.scala.Document("indexName" -> indexName))

      case SQLCommandType.DropTable =>
        provider.dao(getCollection).drop().map(_ => org.mongodb.scala.Document("wasAcknowledged" -> true))

      case _ =>
        throw new IllegalArgumentException("not supported sql command type")
    }
  }

  private def getUpdateOrDeleteFilter: Bson = {
    updateOrDeleteFilter.getOrElse(Map.empty).toMap
  }

  private def convertValue(expression: Expression): Any = {
    expression match {
      case e: net.sf.jsqlparser.expression.LongValue      => e.getValue
      case e: net.sf.jsqlparser.expression.DoubleValue    => e.getValue
      case e: net.sf.jsqlparser.expression.StringValue    => e.getValue
      case e: net.sf.jsqlparser.expression.DateValue      => e.getValue
      case e: net.sf.jsqlparser.expression.TimeValue      => e.getValue
      case e: net.sf.jsqlparser.expression.TimestampValue => e.getValue
      case e: net.sf.jsqlparser.expression.NullValue      => null
      case _ =>
        throw new IllegalArgumentException("not supported value type")
    }
  }

  private def parseWhere(ex: Expression, queryMap: mutable.Map[String, Any]): Unit = {
    ex match {
      case e: EqualsTo =>
        queryMap.put(e.getLeftExpression.toString, Map("$eq" -> convertValue(e.getRightExpression)))
      case e: NotEqualsTo =>
        queryMap.put(e.getLeftExpression.toString, Map("$ne" -> convertValue(e.getRightExpression)))
      case e: GreaterThan =>
        queryMap.put(e.getLeftExpression.toString, Map("$gt" -> convertValue(e.getRightExpression)))
      case e: GreaterThanEquals =>
        queryMap.put(e.getLeftExpression.toString, Map("$gte" -> convertValue(e.getRightExpression)))
      case e: MinorThan =>
        queryMap.put(e.getLeftExpression.toString, Map("$lt" -> convertValue(e.getRightExpression)))
      case e: Between =>
        val fieldName = e.getLeftExpression.toString
        if (e.isNot) {
          queryMap.put(
            "$or",
            List(
              Map(fieldName -> Map("$lte" -> convertValue(e.getBetweenExpressionStart))),
              Map(fieldName -> Map("$gte" -> convertValue(e.getBetweenExpressionEnd)))
            )
          )
        }
        else {
          queryMap.put(
            "$and",
            List(
              Map(fieldName -> Map("$gte" -> convertValue(e.getBetweenExpressionStart))),
              Map(fieldName -> Map("$lte" -> convertValue(e.getBetweenExpressionEnd)))
            )
          )
        }
      case e: MinorThanEquals =>
        queryMap.put(e.getLeftExpression.toString, Map("$lte" -> convertValue(e.getRightExpression)))
      case e: OrExpression =>
        val left  = mutable.Map[String, Any]()
        val right = mutable.Map[String, Any]()
        parseWhere(e.getLeftExpression, left)
        parseWhere(e.getRightExpression, right)
        queryMap.put("$or", List(left, right))
      case e: AndExpression =>
        val left  = mutable.Map[String, Any]()
        val right = mutable.Map[String, Any]()
        parseWhere(e.getLeftExpression, left)
        parseWhere(e.getRightExpression, right)
        queryMap.put("$and", List(left, right))
      case e: Parenthesis =>
        parseWhere(e.getExpression, queryMap)
      case e: InExpression =>
        val value = e.getRightExpression match {
          case l: ParenthesedExpressionList[Expression] => l.asScala.map(convertValue)
          case i: Any                                   => throw new IllegalArgumentException(s"${i.getClass.getSimpleName} not supported")
        }
        val functionName = if (e.isNot) "$nin" else "$in"
        queryMap.put(e.getLeftExpression.toString, Map(functionName -> value))
      case e: IsNullExpression =>
        if (e.isNot) {
          queryMap.put(e.getLeftExpression.toString, Map("$ne" -> null))
        }
        else {
          queryMap.put(e.getLeftExpression.toString, Map("$eq" -> null))
        }
      case _ =>
        throw new IllegalArgumentException("not supported where expression")
    }
  }

  private def convertSelectStatement(select: Select): Unit = {
    select.getSelectBody match {
      case plainSelect: PlainSelect =>
        val selectItems = Option(plainSelect.getSelectItems).map(_.asScala).getOrElse(List.empty)
        val aliasList   = ArrayBuffer[String]()
        sqlCommandType = SQLCommandType.Select
        Option(plainSelect.getGroupBy).foreach(gbEl => {
          val groupBy = gbEl.getGroupByExpressionList.getExpressions.asScala.map(_.toString).toList
          val groupId = mutable.Map[String, Any]()
          val group   = mutable.Map[String, Any]()
          groupBy.foreach(g => groupId += g -> ("$" + g))
          selectItems.foreach { case e: SelectItem[Expression] =>
            val expressionName = e.getExpression.toString
            if (expressionName.contains("count")) {
              group += expressionName -> Map("$sum" -> 1)
            }
            else {
              if (!groupBy.contains(expressionName)) {
                val espr = expressionName.split('(').map(_.trim.replace(")", "")).map(s => ("$" + s))
                group += expressionName -> Map(espr.head -> espr.last)
              }
            }
          }
          val groupMap = Map("_id" -> groupId) ++ group.toMap ++ groupId.keys.map(s => s -> Map("$first" -> ("$" + s))).toMap
          aggregatePipeline += Map("$group" -> groupMap)
        })
        def convertFromItemToTable(fromItem: FromItem): Table = {
          val tableName = Option(fromItem.getAlias).map(a => fromItem.toString.replace(a.toString, "")).getOrElse(fromItem).toString
          new Table(tableName)
        }
        sqlTable = convertFromItemToTable(plainSelect.getFromItem)
        alias = Option(plainSelect.getFromItem.getAlias).map(alias => {
          val aliasName = alias.getName
          aliasList += aliasName
          aggregatePipeline += Map(
            "$project" -> Map(
              "_id"     -> 0,
              aliasName -> "$$ROOT"
            )
          )
          aliasName
        })
        Option(plainSelect.getJoins)
          .map(_.asScala)
          .getOrElse(List.empty)
          .foreach(join => {
            var lookupMap = Map[String, Any]()
            if (join.getOnExpressions != null && !join.getOnExpressions.isEmpty)
              join.getRightItem.getAlias match {
                case null =>
                  lookupMap += "from" -> join.getRightItem.toString
                  lookupMap += "as"   -> join.getRightItem.toString
                case _ =>
                  lookupMap += "from" -> join.getRightItem.toString.replace(join.getRightItem.getAlias.toString, "")
                  lookupMap += "as"   -> join.getRightItem.getAlias.getName
              }
            join.getOnExpressions.asScala.foreach(e => {
              val equalsTo             = e.asInstanceOf[EqualsTo]
              val joinCollectionPrefix = s"${lookupMap("as")}."
              aliasList += lookupMap("as").toString
              val primaryCollectionPrefix = s"${alias.getOrElse(sqlTable)}."
              val expressionList = List(equalsTo.getLeftExpression.toString, equalsTo.getRightExpression.toString)
                .filter(e => e.contains(joinCollectionPrefix) || e.contains(primaryCollectionPrefix))
              if (expressionList.size == 2) {
                expressionList.foreach { exp =>
                  if (exp.contains(primaryCollectionPrefix)) {
                    lookupMap += "localField" -> exp
                  }
                  if (exp.contains(joinCollectionPrefix)) {
                    lookupMap += "foreignField" -> exp.replace(joinCollectionPrefix, "")
                  }
                }
              }
              else {
                throw new IllegalArgumentException("join on expression must contain collection and lookup collection")
              }
            })
            aggregatePipeline += Map("$lookup" -> lookupMap)
            aggregatePipeline += Map("$unwind" -> Map("path" -> s"$$${lookupMap("as")}", "preserveNullAndEmptyArrays" -> false))
          })
        Option(plainSelect.getWhere).foreach { where =>
          val filterQuery = mutable.Map[String, Any]()
          parseWhere(where, filterQuery)
          aggregatePipeline += Map(
            "$match" -> filterQuery
          )
        }
        Option(plainSelect.getOrderByElements).foreach { orderBy =>
          aggregatePipeline += Map(
            "$sort" -> orderBy.asScala.map(e => e.getExpression.toString -> (if (e.isAsc) 1 else -1)).toMap
          )
        }
        Option(plainSelect.getHaving()).foreach { having =>
          val filterQuery = mutable.Map[String, Any]()
          parseWhere(having, filterQuery)
          aggregatePipeline += Map(
            "$match" -> filterQuery
          )
        }
        val hasAllColumns = selectItems.exists(i => i.toString.equalsIgnoreCase("*"))
        if (selectItems.nonEmpty && !hasAllColumns) {
          val addFields = selectItems.filter {
            case e: SelectItem[Expression] =>
              e.getAlias match {
                case null => false
                case _ =>
                  true
              }
            case _ => false
          }
          val fields: Map[String, Any] = addFields
            .map(_.asInstanceOf[SelectItem[Expression]])
            .map(e => e.getAlias.getName -> ("$" + e.getExpression.toString))
            .toMap

          if (fields.nonEmpty) {
            aggregatePipeline += Map("$addFields" -> fields)
          }
          aggregatePipeline += Map(
            "$project" -> selectItems
              .filterNot(s => s.toString.equalsIgnoreCase("*"))
              .map {
                case e: SelectItem[Expression] =>
                  e.getAlias match {
                    case null =>
                      e.getExpression.toString -> 1
                    case _ =>
                      e.getAlias.getName -> 1
                  }
                case _ => throw new IllegalArgumentException("not supported sql command type")
              }
              .toMap
          )

        }
        if (aliasList.nonEmpty) {
          aliasList += "$$ROOT"
          aggregatePipeline += Map(
            "$replaceWith" -> Map("$mergeObjects" -> aliasList.map(string => if (string.startsWith("$")) string else "$" + string).toList)
          )
        }
        Option(plainSelect.getDistinct).foreach { distinct =>
          val groupMap: mutable.Map[String, Any] = mutable.Map()
          selectItems.foreach { case e: SelectItem[Expression] =>
            val expressionName = e.getExpression.toString
            if (expressionName.contains("count")) {
              groupMap += expressionName -> Map("$sum" -> 1)
            }
            else {
              val espr = expressionName.split('(').map(_.trim.replace(")", "")).map(s => ("$" + s))
              if (espr.head.equalsIgnoreCase(espr.last)) {
                groupMap += expressionName -> Map("$first" -> espr.last)
              }
              else {
                groupMap += expressionName -> Map(espr.head -> espr.last)
              }
            }
          }
          groupMap.put("_id", groupMap.keys.map(s => s -> ("$" + s)).toMap)
          aggregatePipeline += Map("$group" -> groupMap.toMap)
          if (plainSelect.getOrderByElements != null) {
            aggregatePipeline += Map(
              "$sort" -> plainSelect.getOrderByElements.asScala.map(e => e.getExpression.toString -> (if (e.isAsc) 1 else -1)).toMap
            )
          }
        }
        Option(plainSelect.getOffset).foreach { offset =>
          aggregatePipeline += Map("$skip" -> convertValue(offset.getOffset))
        }
        Option(plainSelect.getLimit).foreach { limit =>
          aggregatePipeline += Map("$limit" -> convertValue(limit.getRowCount))
        }
      case _ => throw new IllegalArgumentException("not supported sql command type")
    }
  }

  private def convertInsertStatement(insert: Insert): Unit = {
    val columns: List[String] = Option(insert.getColumns).map(_.asScala).getOrElse(List.empty).map(_.getColumnName).toList
    if (columns.isEmpty) {
      throw new IllegalArgumentException("column names must be specified")
    }
    var singleDocumentCreated                 = false
    val baseExpressionList: ExpressionList[_] = insert.getSelect.getValues.getExpressions
    baseExpressionList.asScala.foreach {
      case e: ParenthesedExpressionList[Expression] =>
        val document = mutable.Map[String, Any]()
        columns.foreach(colName => document += colName -> convertValue(e.get(columns.indexOf(colName))))
        documentsToInsert += document.toMap
      case _ =>
        try {
          if (!singleDocumentCreated) {
            val document = mutable.Map[String, Any]()
            columns.foreach(colName => document += colName -> convertValue(baseExpressionList.get(columns.indexOf(colName)).asInstanceOf[Expression]))
            documentsToInsert += document.toMap
          }
          singleDocumentCreated = true
        }
        catch {
          case _: Throwable =>
            throw new IllegalArgumentException("not supported expression list")
        }
    }

    sqlCommandType = SQLCommandType.Insert
    sqlTable = insert.getTable
  }

  private def convertUpdateStatement(update: Update): Unit = {
    val filter = Option(update.getWhere)
      .map { where =>
        val filterQuery = mutable.Map[String, Any]()
        parseWhere(where, filterQuery)
        filterQuery.toMap
      }
      .getOrElse(Map.empty)
    updateOrDeleteFilter = Some(filter)

    val updateSetElement = mutable.Map[String, Any]()
    Option(update.getUpdateSets)
      .map(_.asScala)
      .getOrElse(List.empty)
      .foreach(set => {
        val columns: List[String] = Option(set.getColumns).map(_.asScala).getOrElse(List.empty).map(_.getColumnName).toList
        if (columns.isEmpty) {
          throw new IllegalArgumentException("column names must be specified")
        }
        columns
          .foreach(colName => updateSetElement += colName -> convertValue(set.getValue(columns.indexOf(colName))))
      })
    if (updateSetElement.nonEmpty) {
      this.setElement = Some(updateSetElement.toMap)
    }
    sqlCommandType = SQLCommandType.Update
    sqlTable = update.getTable
  }

  private def convertDeleteStatement(delete: Delete): Unit = {
    val filter = Option(delete.getWhere)
      .map { where =>
        val filterQuery = mutable.Map[String, Any]()
        parseWhere(where, filterQuery)
        filterQuery.toMap
      }
      .getOrElse(Map.empty)
    updateOrDeleteFilter = Some(filter)
    sqlCommandType = SQLCommandType.Delete
    sqlTable = delete.getTable
  }

  private def convertCreateIndexStatement(createIndex: CreateIndex): Unit = {
    sqlTable = createIndex.getTable
    sqlCommandType = SQLCommandType.CreateIndex
    val mongoIndexOptions = IndexOptions()
    val indexToCreate     = Option(createIndex.getIndex).getOrElse(throw new IllegalArgumentException("index must be defined"))
    mongoIndexOptions.name(indexToCreate.getName)
    indexToCreate.getType match {
      case "UNIQUE" =>
        mongoIndexOptions.unique(true)
      case _ =>
        ""
    }
    indexOptions = Some(mongoIndexOptions)
    setElement = Some(ascending(indexToCreate.getColumns.asScala.map(_.getColumnName).toSeq: _*))
  }
}

object MongoSqlQueryHolder {

  def stringToStatement(sql: String, charset: String = "UTF-8") = {
    val stream: java.io.InputStream = new java.io.ByteArrayInputStream(sql.getBytes(charset))
    val jSqlParser                  = new CCJSqlParser(new StreamProvider(stream, charset))
    val statements                  = jSqlParser.Statements().getStatements.asScala
    if (statements.size != 1) {
      throw new IllegalArgumentException("only one statement is supported")
    }
    statements.head
  }

  def apply(statement: net.sf.jsqlparser.statement.Statement): MongoSqlQueryHolder = new MongoSqlQueryHolder(statement)

  def apply(sql: String, charset: String = "UTF-8"): MongoSqlQueryHolder = {
    new MongoSqlQueryHolder(stringToStatement(sql, charset))
  }

}