# SQL Converter to Mongo Query

The MongoSqlQueryHolder provides a way to convert a SQL query to a Mongo query and execute it on a Mongo database.

## Usage

Initialize the query holder with the SQL query you want to analyse or execute.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/sql/SelectSqlSuite.scala#initialize-query-holder

In most cases you simply want to run the query and get the result as a `Seq[Document]`. 
::: tip
The method run returns a classical MongoDb Observable use the implicits to convert it to a `Seq[Document]`.
:::
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/sql/SelectSqlSuite.scala#query-holder-run

You can also get the information about the collection and the keys that are used in the query.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/sql/SelectSqlSuite.scala#extract-collection
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/sql/SelectSqlSuite.scala#select-keys

In some cases you need the information about the function calls in the query, for example in your own [jdbc driver](jdbc-driver.md) implementation. Because the difference of MongoDb and SQL for example a sql `select count(*) from empty-collection` is a list documents with one element and the MongoDb has no document in it.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/sql/SelectSqlSuite.scala#has-function-call
