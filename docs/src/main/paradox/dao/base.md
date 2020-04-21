# MongoDAO Base

With an object extending MongoDAO class all functions on a specific MongoCollection can be easily used.

Base Functions like

* drop
* count
* createIndex

are implemented.

@ref:[Search Functions](search.md) and @ref:[CRUD Functions](crud.md) are available.

##

## Additional Features

### Synchronous Result

All functions support synchronous result handling.

Use pacckage import
```scala
import com.sfxcode.nosql.mongo._
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

@@snip [RestaurantDatabase.scala](/src/test/scala/com/sfxcode/nosql/mongo/operation/AggregationSpec.scala) { #agg_execute }

## Base Functions

### Drop

Drop Collection.

```scala
def drop(): Observable[Void]
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

def dropIndex(keys: Bson): SingleObservable[Void]

// Simple Index delete
def dropIndexForName(name: String): SingleObservable[Void]

```