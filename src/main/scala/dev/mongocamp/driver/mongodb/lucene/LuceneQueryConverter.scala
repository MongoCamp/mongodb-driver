package dev.mongocamp.driver.mongodb.lucene

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.exception.NotSupportedException
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search._
import org.apache.lucene.search.BooleanClause.Occur
import org.joda.time.DateTime
import org.mongodb.scala.bson.conversions.Bson
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._
import scala.util.Try

object LuceneQueryConverter extends LazyLogging {

  def toDocument(query: Query, searchWithValueAndString: Boolean = false): Bson = {
    getMongoDbSearchMap(query, negated = false, searchWithValueAndString)
  }

  def parse(queryString: String, defaultField: String): Query = {
    var analyzer    = new MongoCampLuceneAnalyzer()
    val queryParser = new QueryParser(defaultField, analyzer)
    queryParser.setAllowLeadingWildcard(true)
    val query = queryParser.parse(queryString)
    analyzer.close()
    analyzer = null
    query
  }

  private def getMongoDbSearchMap(query: Query, negated: Boolean, searchWithValueAndString: Boolean): Map[String, Any] = {
    val searchMapResponse = mutable.Map[String, Any]()
    query match {
      case booleanQuery: BooleanQuery     => appendBooleanQueryToSearchMap(searchMapResponse, booleanQuery, searchWithValueAndString, negated)
      case termRangeQuery: TermRangeQuery => appendTermRangeQueryToSearchMap(negated, searchMapResponse, termRangeQuery, searchWithValueAndString)
      case termQuery: TermQuery           => appendTermQueryToSearchMap(negated, searchMapResponse, termQuery, searchWithValueAndString)
      case query: PrefixQuery             => appendPrefixQueryToSearchMap(negated, searchMapResponse, query)
      case query: WildcardQuery           => appendWildCardQueryToSearchMap(negated, searchMapResponse, query)
      case query: PhraseQuery             => appendPhraseQueryToSearchMap(negated, searchMapResponse, query)
      case a: Any =>
        val simpleNameOption = Option(a.getClass.getSimpleName).filterNot(
          s => s.trim.equalsIgnoreCase("")
        )
        if (simpleNameOption.isDefined) {
          logger.error(s"Unexpected QueryType <${a.getClass.getSimpleName}>")
        }
    }
    searchMapResponse.toMap
  }

  private def appendBooleanQueryToSearchMap(
    searchMapResponse: mutable.Map[String, Any],
    booleanQuery: BooleanQuery,
    searchWithValueAndString: Boolean,
    negate: Boolean
  ): Unit = {
    val subQueries  = booleanQuery.clauses().asScala
    val listOfAnd   = ArrayBuffer[Map[String, Any]]()
    val listOfOr    = ArrayBuffer[Map[String, Any]]()
    val listOfNOr   = ArrayBuffer[Map[String, Any]]()
    var nextTypeAnd = true
    subQueries.foreach(
      c => {
        val negateSubquery = (c.occur() == Occur.MUST_NOT)
        val queryMap       = getMongoDbSearchMap(c.query(), negateSubquery, searchWithValueAndString)
        var thisTypeAnd    = true

        if (c.occur == Occur.MUST) {
          thisTypeAnd = true
        }
        else if (c.occur == Occur.SHOULD) {
          thisTypeAnd = false
        }
        else if (c.occur == Occur.MUST_NOT) {
          //                searchMapResponse ++= queryMap
        }
        else {
          logger.error(s"Unexpected Occur <${c.occur.name()}>")
          throw new NotSupportedException(s"${c.occur.name()} currently not supported")
        }

        if (nextTypeAnd && thisTypeAnd) {
          listOfAnd += queryMap
        }
        else {
          listOfOr += queryMap
        }
        nextTypeAnd = thisTypeAnd
      }
    )

    if (listOfAnd.nonEmpty) {
      if (negate) {
        searchMapResponse.put("$nor", listOfAnd.toList)
      }
      else {
        searchMapResponse.put("$and", listOfAnd.toList)
      }
    }
    if (listOfOr.nonEmpty) {
      if (negate) {
        searchMapResponse.put("$nor", listOfOr.toList)
      }
      else {
        searchMapResponse.put("$or", listOfOr.toList)
      }
    }
  }

  private def appendTermRangeQueryToSearchMap(
    negated: Boolean,
    searchMapResponse: mutable.Map[String, Any],
    termRangeQuery: TermRangeQuery,
    searchWithValueAndString: Boolean
  ): Unit = {
    val lowerBoundString = new String(termRangeQuery.getLowerTerm.bytes)
    val lowerBound       = checkAndConvertValue(lowerBoundString)
    val upperBoundString = new String(termRangeQuery.getUpperTerm.bytes)
    val upperBound       = checkAndConvertValue(upperBoundString)

    val searchWithStringValue = searchWithValueAndString && (lowerBoundString != lowerBound || upperBoundString != upperBound)

    val inRangeSearch       = Map("$lte" -> upperBound, "$gte" -> lowerBound)
    val inRangeStringSearch = Map("$lte" -> upperBoundString, "$gte" -> lowerBoundString)
    if (negated) {
      if (searchWithStringValue) {
        searchMapResponse.put(
          "$and",
          List(Map(termRangeQuery.getField -> Map("$not" -> inRangeSearch)), Map(termRangeQuery.getField -> Map("$not" -> inRangeStringSearch)))
        )
      }
      else {
        searchMapResponse.put(termRangeQuery.getField, Map("$not" -> inRangeSearch))
      }
    }
    else {
      if (searchWithStringValue) {
        searchMapResponse.put("$or", List(Map(termRangeQuery.getField -> inRangeSearch), Map(termRangeQuery.getField -> inRangeStringSearch)))
      }
      else {
        searchMapResponse.put(termRangeQuery.getField, inRangeSearch)
      }
    }
  }

  private def appendTermQueryToSearchMap(
    negated: Boolean,
    searchMapResponse: mutable.Map[String, Any],
    termQuery: TermQuery,
    searchWithValueAndString: Boolean
  ): Unit = {
    val convertedValue = checkAndConvertValue(termQuery.getTerm.text())
    if (negated) {
      if (!searchWithValueAndString || convertedValue == termQuery.getTerm.text()) {
        searchMapResponse.put(termQuery.getTerm.field(), Map("$ne" -> convertedValue))
      }
      else {
        searchMapResponse.put(
          "$and",
          List(Map(termQuery.getTerm.field() -> Map("$ne" -> convertedValue)), Map(termQuery.getTerm.field() -> Map("$ne" -> termQuery.getTerm.text())))
        )
      }
    }
    else {
      if (!searchWithValueAndString || convertedValue == termQuery.getTerm.text()) {
        searchMapResponse.put(termQuery.getTerm.field(), Map("$eq" -> convertedValue))
      }
      else {
        searchMapResponse.put(
          "$or",
          List(Map(termQuery.getTerm.field() -> Map("$eq" -> convertedValue)), Map(termQuery.getTerm.field() -> Map("$eq" -> termQuery.getTerm.text())))
        )
      }
    }
  }

  private def appendPrefixQueryToSearchMap(negated: Boolean, searchMapResponse: mutable.Map[String, Any], query: PrefixQuery): Unit = {
    val searchValue                = s"${checkAndConvertValue(query.getPrefix.text())}(.*?)"
    val listOfSearches: List[Bson] = List(Map(query.getField -> generateRegexQuery(s"$searchValue", "i")))
    if (negated) {
      searchMapResponse.put("$nor", listOfSearches)
    }
    else {
      searchMapResponse ++= Map("$and" -> listOfSearches)
    }
  }

  private def appendWildCardQueryToSearchMap(negated: Boolean, searchMapResponse: mutable.Map[String, Any], query: WildcardQuery): Unit = {
    val searchValue = checkAndConvertValue(query.getTerm.text().replace("*", "(.*?)"))
    if (negated) {
      searchMapResponse.put(query.getField, Map("$not" -> generateRegexQuery(s"$searchValue", "i")))
    }
    else {
      searchMapResponse.put(query.getField, generateRegexQuery(s"$searchValue", "i"))
    }
  }

  private def appendPhraseQueryToSearchMap(negated: Boolean, searchMapResponse: mutable.Map[String, Any], query: PhraseQuery): Unit = {
    val listOfSearches = query.getTerms
      .map(
        term => {
          val convertedValue = checkAndConvertValue(term.text())
          if (convertedValue.isInstanceOf[String]) {
            Map(term.field() -> generateRegexQuery(s"(.*?)$convertedValue(.*?)", "i"))
          }
          else {
            Map(term.field() -> Map("$eq" -> convertedValue))
          }
        }
      )
      .toList
    if (negated) {
      searchMapResponse.put("$nor", listOfSearches)
    }
    else {
      searchMapResponse ++= Map("$and" -> listOfSearches)
    }
  }

  private def generateRegexQuery(pattern: String, options: String): Map[String, String] = {
    Map("$regex" -> pattern, "$options" -> options)
  }

  private def checkAndConvertValue(s: String): Any = {

    def checkOrReturn[A <: Any](f: () => A): Option[A] = {
      try {
        val value = f()
        if (value.toString.equals(s)) {
          Option(value)
        }
        else {
          None
        }
      }
      catch {
        case _: Exception => None
      }
    }

    try {
      val convertedValue: Option[Any] =
        (List() ++ checkOrReturn(
          () => s.toDouble
        ) ++ checkOrReturn(
          () => s.toLong
        ) ++ checkOrReturn(
          () => s.toBoolean
        )).headOption
      val response = convertedValue.getOrElse {
        val parsedOptions: List[Date] = Try(new DateTime(s).toDate).toOption.toList ++ datePatters
          .flatMap(
            pattern => {
              try {
                val formatter = new SimpleDateFormat(pattern)
                val r         = Option(formatter.parse(s))
                logger.info(s"parsed date $s with pattern $pattern to $r")
                r
              }
              catch {
                case e: Exception =>
                  None
              }
            }
          )
          .distinct
        parsedOptions.headOption.getOrElse(s)
      }
      response
    }
    catch {
      case _: Throwable =>
        s
    }
  }

  private lazy val datePatters = List(
    "yyyyMMdd'T'HHmmssSSS'Z'",
    "yyyyMMdd'T'HHmmssZ",
    "yyyyMMdd'T'HHmmZ",
    "yyyyMMdd'T'HHmmssSSS",
    "yyyyMMdd'T'HHmmss",
    "yyyyMMdd'T'HHmm",
    "yyyy-MM-dd'T'HH:mm:ss.SSSZZ",
    "yyyy-MM-dd'T'HH:mm:ssZZ",
    "yyyy-MM-dd'T'HH:mmZZ",
    "yyyy-MM-dd'T'HH:mm:ss.SSS",
    "yyyy-MM-dd'T'HH:mm:ss",
    "yyyy-MM-dd'T'HH:mm"
  )
}
