package dev.mongocamp.driver.mongodb.lucene

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.exception.NotSupportedException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import org.apache.lucene.index.Term
import org.apache.lucene.search._
import org.apache.lucene.search.BooleanClause.Occur
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
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
    val analyzer = new MongoCampLuceneAnalyzer()
    try new MongoCampLuceneQueryParser(defaultField, analyzer).parse(queryString)
    finally analyzer.close()
  }

  private def getMongoDbSearchMap(query: Query, negated: Boolean, searchWithValueAndString: Boolean): Map[String, Any] = {
    val searchMapResponse = mutable.Map[String, Any]()
    query match {
      case booleanQuery: BooleanQuery     => appendBooleanQueryToSearchMap(searchMapResponse, booleanQuery, searchWithValueAndString, negated)
      case termRangeQuery: TermRangeQuery => appendTermRangeQueryToSearchMap(negated, searchMapResponse, termRangeQuery, searchWithValueAndString)
      case termQuery: TermQuery           => appendTermQueryToSearchMap(negated, searchMapResponse, termQuery, searchWithValueAndString)
      case query: PrefixQuery             => appendPrefixQueryToSearchMap(negated, searchMapResponse, query)
      case query: WildcardQuery           => appendWildCardQueryToSearchMap(negated, searchMapResponse, query)
      case query: PhraseQuery             => appendPhraseQueryToSearchMap(negated, searchMapResponse, query, searchWithValueAndString)
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
    val text = termQuery.getTerm.text()
    if (text.contains("*")) {
      // wildcards in quoted values are not parsed as WildcardQuery by lucene
      appendWildCardQueryToSearchMap(negated, searchMapResponse, new WildcardQuery(termQuery.getTerm))
    }
    else {
      val convertedValue = checkAndConvertValue(text)
      val field          = termQuery.getTerm.field()
      if (negated) {
        if (!searchWithValueAndString || convertedValue == text) {
          searchMapResponse.put(field, Map("$ne" -> convertedValue))
        }
        else {
          searchMapResponse.put("$and", List(Map(field -> Map("$ne" -> convertedValue)), Map(field -> Map("$ne" -> text))))
        }
      }
      else {
        if (!searchWithValueAndString || convertedValue == text) {
          searchMapResponse.put(field, Map("$eq" -> convertedValue))
        }
        else {
          searchMapResponse.put("$or", List(Map(field -> Map("$eq" -> convertedValue)), Map(field -> Map("$eq" -> text))))
        }
      }
    }
  }

  private def appendPrefixQueryToSearchMap(negated: Boolean, searchMapResponse: mutable.Map[String, Any], query: PrefixQuery): Unit = {
    val listOfSearches: List[Bson] = List(Map(query.getField -> wildcardRegexQuery(s"${query.getPrefix.text()}*")))
    if (negated) {
      searchMapResponse.put("$nor", listOfSearches)
    }
    else {
      searchMapResponse ++= Map("$and" -> listOfSearches)
    }
  }

  private def appendWildCardQueryToSearchMap(negated: Boolean, searchMapResponse: mutable.Map[String, Any], query: WildcardQuery): Unit = {
    val regexQuery = wildcardRegexQuery(query.getTerm.text())
    if (negated) {
      searchMapResponse.put(query.getField, Map("$not" -> regexQuery))
    }
    else {
      searchMapResponse.put(query.getField, regexQuery)
    }
  }

  // a phrase is searched as exact value, MongoCampLuceneQueryParser creates a TermQuery for quoted values, other parsers a PhraseQuery
  private def appendPhraseQueryToSearchMap(
    negated: Boolean,
    searchMapResponse: mutable.Map[String, Any],
    query: PhraseQuery,
    searchWithValueAndString: Boolean
  ): Unit = {
    val value = query.getTerms.map(_.text()).mkString(" ")
    appendTermQueryToSearchMap(negated, searchMapResponse, new TermQuery(new Term(query.getField, value)), searchWithValueAndString)
  }

  private def generateRegexQuery(pattern: String, options: String): Map[String, String] = {
    Map("$regex" -> pattern, "$options" -> options)
  }

  private val regexMetaChars = "\\.[]{}()+-|^$/"

  // the wildcard value has to match the whole value like in lucene, the search is case-insensitive and . matches line breaks
  private def wildcardRegexQuery(value: String): Map[String, String] = generateRegexQuery(s"^${wildcardToRegex(value)}$$", "is")

  private def wildcardToRegex(value: String): String = {
    value.map {
      case '*'                             => "(.*?)"
      case '?'                             => "."
      case c if regexMetaChars.contains(c) => s"\\$c"
      case c                               => c.toString
    }.mkString
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
        parseDate(s).getOrElse(s)
      }
      response
    }
    catch {
      case _: Throwable =>
        s
    }
  }

  private def parseDate(s: String): Option[Date] = {
    Try(new DateTime(s, DateTimeZone.UTC).toDate).toOption.orElse(
      datePatterns.view
        .flatMap(
          pattern => {
            val formatter = new SimpleDateFormat(pattern)
            formatter.setLenient(false)
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
            val position = new ParsePosition(0)
            Option(formatter.parse(s, position)).filter(
              date => position.getIndex == s.length && formatter.format(date).length == s.length
            )
          }
        )
        .headOption
    )
  }

  private lazy val datePatterns = List(
    "yyyyMMdd'T'HHmmssSSSZ",
    "yyyyMMdd'T'HHmmssSSS'Z'",
    "yyyyMMdd'T'HHmmssZ",
    "yyyyMMdd'T'HHmmss'Z'",
    "yyyyMMdd'T'HHmmZ",
    "yyyyMMdd'T'HHmm'Z'",
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
