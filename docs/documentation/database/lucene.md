# Lucene Query

MongoCamp Mongo Driver support the usage of [Lucene Query](https://lucene.apache.org/) to search in the MongoDb.

## Usage
### Explicit Usage
The LuceneConverter has the methods to parse a String to and `Query` and a other to the document conversion.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSuite.scala#lucene-parser-with-explicit

### Implicit Usage
Like the Map to Bson conversion there is also an implicit method to convert `Query` to find Bson. 

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSuite.scala#lucene-parser-with-implicit

### Parse String to Query
We have an individual parser to parse an string to Lucene Query, because the default Lucene Analyser is case-insensitive and convert all search data into lower case. So the best way to seach in MongoDb with Lucene Query is to use this code. 

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSuite.scala#lucene-parser

## Read More
[Lucene Cheatsheet](https://www.lucenetutorial.com/lucene-query-syntax.html)