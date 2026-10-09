package dev.mongocamp.driver.mongodb.lucene
import java.io.Reader
import org.apache.lucene.analysis._
import org.apache.lucene.util.WeakIdentityMap

/** Analyzer for LuceneQueryConverter, the terms are not lower cased.
  *
  * Lucene creates the token stream components for each thread, so the analyzer gets a factory and creates a new tokenizer for each thread. This makes the
  * analyzer thread safe.
  *
  * @param stopWords
  *   terms that are removed from the query
  * @param tokenizerFactory
  *   creates a new tokenizer that splits the values to terms, the default MongoCampTokenizer splits only at whitespace. The factory must return a new instance
  *   on each call, otherwise an IllegalStateException is thrown.
  */
class MongoCampLuceneAnalyzer(
  stopWords: CharArraySet = CharArraySet.EMPTY_SET,
  tokenizerFactory: () => Tokenizer = MongoCampLuceneAnalyzer.defaultTokenizerFactory
) extends StopwordAnalyzerBase {

  private val createdTokenizers = WeakIdentityMap.newConcurrentHashMap[Tokenizer, java.lang.Boolean]()

  override protected def createComponents(fieldName: String): Analyzer.TokenStreamComponents = {
    val tokenizer = tokenizerFactory()
    if (createdTokenizers.put(tokenizer, java.lang.Boolean.TRUE) != null) {
      throw new IllegalStateException("tokenizerFactory must create a new Tokenizer on each call, a Tokenizer instance can not be shared")
    }
    val tok: TokenStream = new StopFilter(tokenizer, stopWords)
    new Analyzer.TokenStreamComponents((r: Reader) => tokenizer.setReader(r), tok)
  }

  override protected def normalize(fieldName: String, in: TokenStream): TokenStream = in

  private[lucene] def trackedTokenizerCount: Int = createdTokenizers.size()

}

object MongoCampLuceneAnalyzer {
  private val defaultMaxTokenLength: Int = 255

  val defaultTokenizerFactory: () => Tokenizer = () => new MongoCampTokenizer(defaultMaxTokenLength)
}
