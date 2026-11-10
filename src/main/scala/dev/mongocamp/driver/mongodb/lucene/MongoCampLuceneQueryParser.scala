package dev.mongocamp.driver.mongodb.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.FieldExistsQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TermRangeQuery
import org.apache.lucene.search.WildcardQuery
import org.apache.lucene.util.BytesRef

/** QueryParser for LuceneQueryConverter. Leading wildcards are allowed and quoted values like `name:"Hallo Welt"` are searched as exact value, they are not
  * split into terms by the analyzer. Additionally supported are the comparison operators `>`, `>=`, `<` and `<=` (`age:>=18`) and `_exists_:field`.
  */
class MongoCampLuceneQueryParser(defaultField: String, analyzer: Analyzer = new MongoCampLuceneAnalyzer()) extends QueryParser(defaultField, analyzer) {
  setAllowLeadingWildcard(true)

  override protected def getFieldQuery(field: String, queryText: String, quoted: Boolean): Query = {
    if (field == MongoCampLuceneQueryParser.ExistsField) {
      new FieldExistsQuery(queryText)
    }
    else {
      queryText match {
        case MongoCampLuceneQueryParser.Comparison(operator, value) if !quoted => comparisonQuery(field, operator, new BytesRef(value))
        case _                                                                 => getValueQuery(field, queryText, quoted)
      }
    }
  }

  private def comparisonQuery(field: String, operator: String, bound: BytesRef): Query = {
    operator match {
      case ">"  => new TermRangeQuery(field, bound, null, false, true)
      case ">=" => new TermRangeQuery(field, bound, null, true, true)
      case "<"  => new TermRangeQuery(field, null, bound, true, false)
      case _    => new TermRangeQuery(field, null, bound, true, true)
    }
  }

  private def getValueQuery(field: String, queryText: String, quoted: Boolean): Query = {
    if (quoted && queryText.contains("*")) {
      new WildcardQuery(new Term(field, MongoCampLuceneQueryParser.escape(queryText, "\\?")))
    }
    else if (quoted) {
      new TermQuery(new Term(field, queryText))
    }
    else {
      super.getFieldQuery(field, queryText, quoted)
    }
  }

  override protected def newTermQuery(term: Term, boost: Float): Query = {
    if (term.text().contains("*")) {
      super.newTermQuery(new Term(term.field(), MongoCampLuceneQueryParser.escape(term.text(), "\\*?")), boost)
    }
    else {
      super.newTermQuery(term, boost)
    }
  }
}

object MongoCampLuceneQueryParser {

  val ExistsField = "_exists_"

  private val Comparison = "^(>=|<=|>|<)(.+)$".r

  private def escape(value: String, charsToEscape: String): String = {
    value
      .map(
        c => if (charsToEscape.contains(c)) s"\\$c" else c.toString
      )
      .mkString
  }

}
