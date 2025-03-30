package dev.mongocamp.driver.mongodb.lucene
import java.io.Reader
import org.apache.lucene.analysis._
import org.apache.lucene.analysis.standard.StandardTokenizer

class MongoCampLuceneAnalyzer(stopWords: CharArraySet = CharArraySet.EMPTY_SET, maxTokenLength: Int = MongoCampLuceneAnalyzer.defaultMaxTokenLength)
    extends StopwordAnalyzerBase {

  override protected def createComponents(fieldName: String): Analyzer.TokenStreamComponents = {
    val src = new StandardTokenizer
    src.setMaxTokenLength(maxTokenLength)
    val tok: TokenStream = new StopFilter(src, stopWords)
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
