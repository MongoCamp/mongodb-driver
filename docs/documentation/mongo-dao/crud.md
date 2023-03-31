# CRUD Functions

## Info

[MongoDAO](index.md) CRUD Collection functions.

## Create

```scala
def insertOne(value: A): Observable[Void]

def insertOne(value: A), options: InsertOneOptions: Observable[Void]

def insertMany(values: Seq[A]): Observable[Void]

def insertMany(values: Seq[A], options: InsertManyOptions): Observable[Void]
```

## Update

```scala
def replaceOne(value: A): Observable[UpdateResult]

def replaceOne(value: A, options: ReplaceOptions): Observable[UpdateResult]
```

## Delete

```scala
def deleteOne(value: A): Observable[DeleteResult]

def deleteOne(filter: Bson): Observable[DeleteResult]

def deleteMany(filter: Bson): Observable[DeleteResult]

def deleteAll(): Observable[DeleteResult]
```