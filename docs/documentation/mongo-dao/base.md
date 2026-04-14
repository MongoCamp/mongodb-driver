# MongoDAO Base

With an object extending MongoDAO class all functions on a specific MongoCollection can be easily used.

Base Functions like

* drop
* count
* createIndex

are implemented.

[Search Functions](search.md), [CRUD Functions](crud.md), [Find-and-Modify](find-and-modify.md), and [Change Streams](change-streams.md) are available.

## Additional Features

### Synchronous Result

All functions support synchronous result handling.

Use package import

```scala
import dev.mongocamp.driver.mongodb._
```

and append

* .result() for Single Result object
* .resultList() for List result object

or use implicit conversion:

```scala
def restaurantsSize: Long = RestaurantDAO.count()
```

### Raw Support

Sometimes we need Raw Support (DAO maps to Document).
This is automatically included in the MongoDAO class.
Simply call Raw on your DAO Object.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/operation/AggregationSuite.scala#agg_execute

## Base Functions

### Drop

Drop Collection.

```scala
def drop(): SingleObservable[Unit]
```

### Count

Count of collection with optional filter.

```scala
def count(filter: Bson = Document()): Observable[Long]
```

### Indexes

```scala
def createIndex(key: Bson, options: IndexOptions = IndexOptions()): SingleObservable[String]

// Simple Index creation
def createIndexForField(field: String, sortAscending: Boolean = true): SingleObservable[String]

def dropIndex(keys: Bson): SingleObservable[Unit]

// Simple Index delete
def dropIndexForName(name: String): SingleObservable[Unit]
```

### Column Names

Detect the field names present in a collection (uses an aggregation pipeline internally):

```scala
def columnNames(sampleSize: Int = 0, maxWait: Int = DefaultMaxWait): List[String]
```

Pass `sampleSize > 0` to scan only a random sample for better performance on large collections.

### Import JSON

Bulk-import a newline-delimited JSON file (one document per line):

```scala
def importJsonFile(file: better.files.File): SingleObservable[BulkWriteResult]
```
