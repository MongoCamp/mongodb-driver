package dev.mongocamp.driver.mongodb.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery

/** QueryParser for LuceneQueryConverter. Leading wildcards are allowed and quoted values like `name:"Hallo Welt"` are searched as exact value, they are not
  * split into terms by the analyzer.
  */
class MongoCampLuceneQueryParser(defaultField: String, analyzer: Analyzer = new MongoCampLuceneAnalyzer()) extends QueryParser(defaultField, analyzer) {
  setAllowLeadingWildcard(true)

  override protected def getFieldQuery(field: String, queryText: String, quoted: Boolean): Query = {
    if (quoted) {
      new TermQuery(new Term(field, queryText))
    }
    else {
      super.getFieldQuery(field, queryText, quoted)
    }
  }
}
