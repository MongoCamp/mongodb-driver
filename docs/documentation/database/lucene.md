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

### Analyzer and Tokenizer
`LuceneQueryConverter.parse` uses the `MongoCampLuceneAnalyzer` with its default settings. The analyzer gets a factory for the tokenizer as parameter, so the way a search value is split into terms is never chosen silently.

| Parameter          | Default                                                   | Description                                                       |
|--------------------|-----------------------------------------------------------|-------------------------------------------------------------------|
| `stopWords`        | `CharArraySet.EMPTY_SET`                                  | Terms that are removed from the query.                            |
| `tokenizerFactory` | `MongoCampLuceneAnalyzer.defaultTokenizerFactory`         | Creates the Lucene `Tokenizer` that splits the values to terms.   |

The default factory creates a `MongoCampWhitespaceTokenizer` with a `maxTokenLength` of 255. It splits values only at whitespace, tokens longer than `maxTokenLength` are split into chunks of `maxTokenLength` characters.

To use another tokenizer, create the analyzer with a factory for it, parse the query with the Lucene `QueryParser` and convert the result with `LuceneQueryConverter.toDocument`.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSuite.scala#lucene-parser-with-tokenizer

The analyzer is thread safe. Lucene creates the token stream components for each thread, so the analyzer calls the factory for each thread and every thread uses its own tokenizer.

::: warning
The factory must create a new `Tokenizer` on each call. A `Tokenizer` instance can not be shared, if the factory returns an instance a second time the analyzer throws an `IllegalStateException`.
:::

### Values
With the default tokenizer values are split only at whitespace, so values like email addresses (`email:john.doe@example.com`) or dates with time zone offset (`registered:20140419T224427000\+0200`) are searched as one value. Leading and trailing single quotes are removed (`'value'` matches `value`).

Wildcard, prefix and phrase queries are converted to regular expressions. `*` and `?` are used as wildcards, all other regular expression characters like `.` or `+` are escaped.

Date values are parsed as ISO date (`2014-04-19T22:44:27+02:00`) or in the basic format (`20140419T224427000+0200`). Date values without time zone offset are interpreted as UTC.

## Read More
[Lucene Cheatsheet](https://www.lucenetutorial.com/lucene-query-syntax.html)