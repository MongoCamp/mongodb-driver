package dev.mongocamp.driver.mongodb.lucene

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute
import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.Tokenizer
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._
import scala.util.Try

class MongoCampWhitespaceTokenizerSuite extends munit.FunSuite {

  private def tokens(text: String, analyzer: MongoCampLuceneAnalyzer = new MongoCampLuceneAnalyzer()): List[(String, Int, Int)] = {
    val stream = analyzer.tokenStream("field", text)
    val term   = stream.addAttribute(classOf[CharTermAttribute])
    val offset = stream.addAttribute(classOf[OffsetAttribute])
    val result = ArrayBuffer[(String, Int, Int)]()
    stream.reset()
    while (stream.incrementToken())
      result += ((term.toString, offset.startOffset(), offset.endOffset()))
    stream.end()
    stream.close()
    result.toList
  }

  test("split only at whitespace") {
    assertEquals(
      tokens("john.doe@example.com  2014-04-19T22:44:27+02:00\tjohn+tag@example.com\n"),
      List(("john.doe@example.com", 0, 20), ("2014-04-19T22:44:27+02:00", 22, 47), ("john+tag@example.com", 48, 68))
    )
  }

  test("remove leading and trailing single quotes") {
    assertEquals(tokens("'value1' 'a b' o'brien@example.com ''"), List(("value1", 1, 7), ("a", 10, 11), ("b", 12, 13), ("o'brien@example.com", 15, 34)))
  }

  test("empty and whitespace only input has no tokens") {
    assertEquals(tokens(""), List())
    assertEquals(tokens("   \t\n"), List())
  }

  test("split tokens longer than max token length") {
    assertEquals(
      tokens("abcdefg hi", new MongoCampLuceneAnalyzer(tokenizerFactory = () => new MongoCampWhitespaceTokenizer(3))),
      List(("abc", 0, 3), ("def", 3, 6), ("g", 6, 7), ("hi", 8, 10))
    )
  }

  test("tokenizer can be reused") {
    val analyzer = new MongoCampLuceneAnalyzer()
    assertEquals(tokens("first value", analyzer), List(("first", 0, 5), ("value", 6, 11)))
    assertEquals(tokens("second", analyzer), List(("second", 0, 6)))
    analyzer.close()
  }

  test("analyzer can be used in multiple threads at the same time") {
    val analyzer   = new MongoCampLuceneAnalyzer()
    val threads    = 16
    val iterations = 500
    val start      = new CountDownLatch(1)
    val executor   = Executors.newFixedThreadPool(threads)
    try {
      val futures = (0 until threads).map(
        thread =>
          executor.submit(new Callable[List[String]] {
            override def call(): List[String] = {
              start.await()
              (0 until iterations).toList.flatMap(
                i => {
                  val expected = List(s"thread$thread", s"value$i@example.com")
                  val result   = tokens(s"thread$thread value$i@example.com", analyzer).map(_._1)
                  if (result == expected) None else Some(s"expected $expected but was $result")
                }
              )
            }
          })
      )
      start.countDown()
      val errors = futures.flatMap(
        future => Try(future.get(60, TimeUnit.SECONDS)).fold(e => List(e.toString), identity)
      )
      assertEquals(errors.take(3).toList, List.empty[String])
    }
    finally {
      executor.shutdownNow()
      analyzer.close()
    }
  }

  test("analyzer creates a tokenizer for each thread") {
    val createdTokenizers = new java.util.concurrent.CopyOnWriteArrayList[Tokenizer]()
    val analyzer = new MongoCampLuceneAnalyzer(tokenizerFactory = () => {
      val tokenizer = new MongoCampWhitespaceTokenizer(255)
      createdTokenizers.add(tokenizer)
      tokenizer
    })
    assertEquals(tokens("a b", analyzer).map(_._1), List("a", "b"))
    assertEquals(tokens("c d", analyzer).map(_._1), List("c", "d"))
    assertEquals(createdTokenizers.size(), 1)
    var otherThreadResult: Try[List[String]] = null
    val thread = new Thread(
      () => otherThreadResult = Try(tokens("e f", analyzer).map(_._1))
    )
    thread.start()
    thread.join()
    assertEquals(otherThreadResult.get, List("e", "f"))
    assertEquals(createdTokenizers.size(), 2)
    assert(!(createdTokenizers.get(0) eq createdTokenizers.get(1)))
    analyzer.close()
  }

  test("analyzer rejects a tokenizer factory that returns the same instance") {
    val sharedTokenizer = new MongoCampWhitespaceTokenizer(255)
    val analyzer        = new MongoCampLuceneAnalyzer(tokenizerFactory = () => sharedTokenizer)
    assertEquals(tokens("a b", analyzer).map(_._1), List("a", "b"))
    var otherThreadResult: Try[List[String]] = null
    val thread = new Thread(
      () => otherThreadResult = Try(tokens("c d", analyzer).map(_._1))
    )
    thread.start()
    thread.join()
    assert(otherThreadResult.failed.get.isInstanceOf[IllegalStateException], otherThreadResult.toString)
    analyzer.close()
  }

  test("analyzer does not keep the tokenizers of finished threads") {
    val analyzer = new MongoCampLuceneAnalyzer()
    (0 until 2000).foreach(
      i => {
        val thread = new Thread(
          () => {
            tokens(s"value$i", analyzer)
            ()
          }
        )
        thread.start()
        thread.join()
      }
    )
    val countBeforeGc = analyzer.trackedTokenizerCount
    var count         = countBeforeGc
    var attempts      = 0
    while (count > 50 && attempts < 50) {
      System.gc()
      Thread.sleep(20)
      count = analyzer.trackedTokenizerCount
      attempts += 1
    }
    println(s"tracked tokenizers after 2000 threads: $countBeforeGc before gc, $count after gc")
    assert(count <= 50, s"$count tokenizers are still tracked")
    analyzer.close()
  }

  test("default tokenizer factory creates a new tokenizer on each call") {
    val first  = MongoCampLuceneAnalyzer.defaultTokenizerFactory()
    val second = MongoCampLuceneAnalyzer.defaultTokenizerFactory()
    assert(first.isInstanceOf[MongoCampWhitespaceTokenizer])
    assert(!(first eq second))
  }

  test("remove stop words") {
    val stopWords = new CharArraySet(List("and").asJava, true)
    assertEquals(tokens("apple and banana", new MongoCampLuceneAnalyzer(stopWords)).map(_._1), List("apple", "banana"))
  }

}
