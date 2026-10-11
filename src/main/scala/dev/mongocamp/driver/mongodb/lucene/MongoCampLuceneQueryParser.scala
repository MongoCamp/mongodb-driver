package dev.mongocamp.driver.mongodb.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.WildcardQuery

/** QueryParser for LuceneQueryConverter. Leading wildcards are allowed and quoted values like `name:"Hallo Welt"` are searched as exact value, they are not
  * split into terms by the analyzer.
  */
class MongoCampLuceneQueryParser(defaultField: String, analyzer: Analyzer = new MongoCampLuceneAnalyzer()) extends QueryParser(defaultField, analyzer) {
  setAllowLeadingWildcard(true)

  override protected def getFieldQuery(field: String, queryText: String, quoted: Boolean): Query = {
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

  private def escape(value: String, charsToEscape: String): String = {
    value
      .map(
        c => if (charsToEscape.contains(c)) s"\\$c" else c.toString
      )
      .mkString
  }

}
