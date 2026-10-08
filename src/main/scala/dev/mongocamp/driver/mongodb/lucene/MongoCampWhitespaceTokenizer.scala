package dev.mongocamp.driver.mongodb.lucene

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute
import org.apache.lucene.analysis.Tokenizer

/** Splits the input only at whitespace, so values like email addresses or dates with time zone offset stay one token. Tokens longer than maxTokenLength are
  * split into chunks of maxTokenLength. Leading and trailing single quotes are removed, so 'value' matches value.
  */
class MongoCampWhitespaceTokenizer(maxTokenLength: Int) extends Tokenizer {
  private val termAttribute   = addAttribute(classOf[CharTermAttribute])
  private val offsetAttribute = addAttribute(classOf[OffsetAttribute])

  private val NoPendingChar = -2
  private val SingleQuote   = '\''

  private var pendingChar = NoPendingChar
  private var offset      = 0
  private var finalOffset = 0

  private def nextChar(): Int = {
    if (pendingChar != NoPendingChar) {
      val c = pendingChar
      pendingChar = NoPendingChar
      c
    }
    else {
      input.read()
    }
  }

  override def incrementToken(): Boolean = {
    clearAttributes()
    var tokenFound = false
    var endOfInput = false
    while (!tokenFound && !endOfInput) {
      var c = nextChar()
      while (c != -1 && Character.isWhitespace(c)) {
        offset += 1
        c = nextChar()
      }
      if (c == -1) {
        finalOffset = correctOffset(offset)
        endOfInput = true
      }
      else {
        val start = offset
        val term  = new java.lang.StringBuilder()
        while (c != -1 && !Character.isWhitespace(c) && term.length() < maxTokenLength) {
          term.append(c.toChar)
          offset += 1
          c = nextChar()
        }
        pendingChar = c
        var termStart = 0
        var termEnd   = term.length()
        while (termStart < termEnd && term.charAt(termStart) == SingleQuote)
          termStart += 1
        while (termEnd > termStart && term.charAt(termEnd - 1) == SingleQuote)
          termEnd -= 1
        if (termStart < termEnd) {
          termAttribute.setEmpty().append(term, termStart, termEnd)
          offsetAttribute.setOffset(correctOffset(start + termStart), correctOffset(start + termEnd))
          tokenFound = true
        }
      }
    }
    tokenFound
  }

  override def end(): Unit = {
    super.end()
    offsetAttribute.setOffset(finalOffset, finalOffset)
  }

  override def reset(): Unit = {
    super.reset()
    pendingChar = NoPendingChar
    offset = 0
    finalOffset = 0
  }
}
