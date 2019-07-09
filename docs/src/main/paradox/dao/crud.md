# CRUD Functions

## Info

@ref:[MongoDAO](index.md) CRUD Collection functions.

## Create

```scala

def insertOne(value: A): Observable[Completed]

def insertOne(value: A), options: InsertOneOptions: Observable[Completed]

def insertMany(values: Seq[A]): Observable[Completed]

def insertMany(values: Seq[A], options: InsertManyOptions): Observable[Completed]

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