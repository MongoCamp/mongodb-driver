# Lucene Query

MongoCamp Mongo Driver support the usage of [Lucene Query](https://lucene.apache.org/) to search in the MongoDb.

## Usage
Fist you have the add the lucene-queryparser Dependency to your build.sbt (without the provided marker).

<<< @/../build.sbt#lucene-dependency{scala}

### Explicit Usage
The LuceneConverter has the methods to parse a String to and `Query` and a other to the document conversion.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSpec.scala#lucene-parser-with-explicit

### Implicit Usage
Like the Map to Bson conversion there is also an implicit method to convert `Query` to find Bson. 

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSpec.scala#lucene-parser-with-implicit

## Read More
[Lucene Cheatsheet](https://www.lucenetutorial.com/lucene-query-syntax.html)