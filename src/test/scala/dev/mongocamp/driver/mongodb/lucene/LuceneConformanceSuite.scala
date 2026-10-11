package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.MongoDAO
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.ByteBuffersDirectory
import org.mongodb.scala.Document
import scala.jdk.CollectionConverters._

class LuceneConformanceSuite extends munit.FunSuite {

  private object ConformanceDAO extends MongoDAO[Document](TestDatabase.provider, "lucene-conformance")

  private val values = List(
    "mary-jane smith",
    "mary-jane",
    "maryjane",
    "a+b@example.com",
    "aab@example.com",
    "ab@example.com",
    "x@my-company.org",
    "x@my-companyxorg",
    "+15551234",
    "0015551234",
    "100*",
    "1000",
    "100",
    "100**",
    "a?b",
    "axb",
    "a.b",
    "ab",
    "a b",
    "a\\b",
    "c\\temp",
    "ctemp",
    "d\\temp\\",
    "\\",
    "(1+1):2",
    "a[b]c",
    "a{b}c",
    "a^b",
    "a$b",
    "a|b",
    "a||b",
    "a&b",
    "a&&b",
    "a/b",
    "path/to/file",
    "a~b",
    "a!b",
    "a\"b",
    "a:b",
    "a=b",
    "a<b>",
    "a#b",
    "a%b",
    "a'b",
    "a,b",
    "a;b",
    "x",
    "",
    "line\nbreak",
    "tab\tx",
    "😀",
    "a😀b",
    "café",
    "a*b*c",
    "a**b",
    "a-b-c"
  )

  private val queries = List(
    """value:*mary\-jane*""",
    """value:mary\-jane*""",
    """value:*\-jane""",
    """value:*a\+b@example.com*""",
    """value:*@my\-company.org""",
    """value:*\+1555*""",
    """value:\+1555*""",
    """value:*100\**""",
    """value:100\**""",
    """value:100\*?""",
    """value:*\*\**""",
    """value:*a\?b*""",
    """value:a\??""",
    """value:*\\*""",
    """value:*\\temp*""",
    """value:c\\te*""",
    """value:*temp\\""",
    """value:\\*""",
    """value:*\(1\+1\)*""",
    """value:\(1\+1\)\:*""",
    """value:*\[b\]*""",
    """value:a\{b\}*""",
    """value:a\^*""",
    """value:a$*""",
    """value:*\|*""",
    """value:a\|\|*""",
    """value:*\&*""",
    """value:a\&\&?""",
    """value:*\/*""",
    """value:path\/*""",
    """value:*\/file""",
    """value:a\~*""",
    """value:a\!*""",
    """value:*\"*""",
    """value:*\:*""",
    """value:a=*""",
    """value:a<*""",
    """value:a#*""",
    """value:a%*""",
    """value:a'*""",
    """value:a,*""",
    """value:a;*""",
    """value:*\ *""",
    """value:a\ ?""",
    """value:\m\a\r\y*""",
    """value:*é""",
    """value:a?b""",
    """value:a??""",
    """value:?""",
    """value:??""",
    """value:*""",
    """value:a*b*c""",
    """value:a*b""",
    """value:*a.b*""",
    """value:*.org""",
    """value:line?break""",
    """value:*break""",
    """value:tab?x""",
    """value:a?b*""",
    """value:caf?""",
    """value:*-b-*""",
    """value:mary\-jane""",
    """value:a\+b@example.com""",
    """value:100\*""",
    """value:100\*\*""",
    """value:a\?b""",
    """value:a\\b""",
    """value:\\""",
    """value:d\\temp\\""",
    """value:\(1\+1\)\:2""",
    """value:a\[b\]c""",
    """value:a\{b\}c""",
    """value:a\^b""",
    """value:a\|\|b""",
    """value:a\&\&b""",
    """value:a\/b""",
    """value:a\~b""",
    """value:a\!b""",
    """value:a\"b""",
    """value:a\:b""",
    """value:a\ b""",
    """value:café""",
    """value:100""",
    """value:\+15551234""",
    """value:a-b-c""",
    """value:mary\-jane* AND value:*smith""",
    """value:*\+1555* OR value:100\*"""
  )

  private lazy val luceneIndex: ByteBuffersDirectory = {
    val directory = new ByteBuffersDirectory()
    val writer    = new IndexWriter(directory, new IndexWriterConfig(new MongoCampLuceneAnalyzer()))
    values.zipWithIndex.foreach {
      case (value, index) =>
        val document = new org.apache.lucene.document.Document()
        document.add(new StringField("id", index.toString, Field.Store.YES))
        document.add(new StringField("value", value, Field.Store.NO))
        writer.addDocument(document)
    }
    writer.close()
    directory
  }

  private def luceneSearch(queryString: String): List[Int] = {
    val parser = new QueryParser("value", new MongoCampLuceneAnalyzer())
    parser.setAllowLeadingWildcard(true)
    val reader = DirectoryReader.open(luceneIndex)
    try {
      val searcher = new IndexSearcher(reader)
      val topDocs  = searcher.search(parser.parse(queryString), values.size)
      val fields   = searcher.storedFields()
      topDocs.scoreDocs.toList
        .map(
          scoreDoc => fields.document(scoreDoc.doc).get("id").toInt
        )
        .sorted
    }
    finally reader.close()
  }

  private def mongoSearch(queryString: String): List[Int] = {
    val query = LuceneQueryConverter.toDocument(LuceneQueryConverter.parse(queryString, "value"), searchWithValueAndString = true)
    ConformanceDAO.find(query).resultList().map(_.getIntValue("id")).sorted
  }

  override def beforeAll(): Unit = {
    ConformanceDAO.drop().result()
    ConformanceDAO
      .insertMany(values.zipWithIndex.map {
        case (value, index) => Document("id" -> index, "value" -> value)
      })
      .result()
  }

  override def afterAll(): Unit = {
    ConformanceDAO.drop().result()
  }

  queries.foreach(
    queryString =>
      test(s"same result as lucene: $queryString") {
        def show(ids: List[Int]) = ids.map(values(_))
        assertEquals(show(mongoSearch(queryString)), show(luceneSearch(queryString)))
      }
  )

  test("every value is found by at least one query") {
    val found = queries.flatMap(luceneSearch).toSet
    assertEquals(values.indices.filterNot(found.contains).map(values(_)).toList, List())
  }

}
