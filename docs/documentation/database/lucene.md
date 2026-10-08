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

To use another tokenizer, create the analyzer with a factory for it, parse the query with the `MongoCampLuceneQueryParser` and convert the result with `LuceneQueryConverter.toDocument`. The `MongoCampLuceneQueryParser` is a Lucene `QueryParser` that allows leading wildcards and searches quoted values as exact value.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSuite.scala#lucene-parser-with-tokenizer

The analyzer is thread safe. Lucene creates the token stream components for each thread, so the analyzer calls the factory for each thread and every thread uses its own tokenizer.

::: warning
The factory must create a new `Tokenizer` on each call. A `Tokenizer` instance can not be shared, if the factory returns an instance a second time the analyzer throws an `IllegalStateException`.
:::

### Exact Values
A quoted value is searched as exact value, `name:"Latasha Mcmillan"` finds only documents with exactly this name. The quoted value is not split into terms, so the search is case-sensitive and whitespace is kept as it is. Quotes inside the value are escaped with a backslash (`name:"Hallo \"Welt\""`).

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/lucene/LuceneSearchSuite.scala#lucene-exact-value

A quoted value with `*` is searched as wildcard query, `name:"Latasha *millan"` finds `Latasha Mcmillan`. A `?` in a quoted value is no wildcard, so `name:"Wie geht's?"` is searched as exact value.

If the query is parsed by another parser, like the Lucene `QueryParser`, quoted values are phrase queries. They are searched as exact value too, but the terms of the phrase are joined by a single space.

### Values
With the default tokenizer values are split only at whitespace, so values like email addresses (`email:john.doe@example.com`) or dates with time zone offset (`registered:20140419T224427000\+0200`) are searched as one value. Leading and trailing single quotes are removed (`'value'` matches `value`).

Wildcard and prefix queries are converted to regular expressions. `*` and `?` are used as wildcards, all other regular expression characters like `.` or `+` are escaped.

Like in Lucene the wildcard value has to match the whole value. The search is case-insensitive and `*` matches line breaks too.

| Query               | Search                                 |
|---------------------|----------------------------------------|
| `name:John*`        | `name` starts with `John`              |
| `name:*Dowe`        | `name` ends with `Dowe`                |
| `name:"*John Dowe*"`| `name` contains `John Dowe`            |
| `name:J?hn`         | `name` is `J` + one character + `hn`   |

::: tip
A wildcard at the beginning of the value can not use an index of the field, so the search is slow on large collections.
:::

Date values are parsed as ISO date (`2014-04-19T22:44:27+02:00`) or in the basic format (`20140419T224427000+0200`). Date values without time zone offset are interpreted as UTC.

## Read More
[Lucene Cheatsheet](https://www.lucenetutorial.com/lucene-query-syntax.html)