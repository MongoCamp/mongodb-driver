# MongoDAO - Base Functions

## Info

Base Collection functions. All functions support synchronous result handling (add Result to function name, e.g. drop -> dropResult).

## Drop

Drop Collection.

```scala
def drop(): Observable[Completed]
```

## Count

Count of collection with optional filter.

```scala
def count(filter: Bson = Document()): Observable[Long]
```

## Indexes

```scala

def createIndex(key: Bson, options: IndexOptions = IndexOptions()): SingleObservable[String]

// Simple Index creation
def createIndexForField(field: String, sortAscending: Boolean = true): SingleObservable[String]

def dropIndex(keys: Bson): SingleObservable[Completed]

// Simple Index delete
def dropIndexForName(name: String): SingleObservable[Completed]

```