package dev.mongocamp.driver.mongodb.lucene
import org.apache.lucene.analysis._
import org.apache.lucene.analysis.standard.StandardTokenizer

import java.io.Reader

class MongoCampLuceneAnalyzer(stopWords: CharArraySet = CharArraySet.EMPTY_SET, maxTokenLength: Int = MongoCampLuceneAnalyzer.defaultMaxTokenLength)
    extends StopwordAnalyzerBase {

  override protected def createComponents(fieldName: String): Analyzer.TokenStreamComponents = {
    val src = new StandardTokenizer
    src.setMaxTokenLength(maxTokenLength)
    val tok: TokenStream = new StopFilter(src, stopwords)
    new Analyzer.TokenStreamComponents(
      (r: Reader) => {
        src.setMaxTokenLength(maxTokenLength)
        src.setReader(r)

      },
      tok
    )
  }

  override protected def normalize(fieldName: String, in: TokenStream): TokenStream = in

}

object MongoCampLuceneAnalyzer {
  private val defaultMaxTokenLength: Int = 255
}
