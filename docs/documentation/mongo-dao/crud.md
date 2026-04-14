# CRUD Functions

## Info

[MongoDAO](index.md) CRUD Collection functions.

## Create

```scala
def insertOne(value: A): Observable[InsertOneResult]
def insertOne(value: A, options: InsertOneOptions): Observable[InsertOneResult]

def insertMany(values: Seq[A]): Observable[InsertManyResult]
def insertMany(values: Seq[A], options: InsertManyOptions): Observable[InsertManyResult]
```

## Update

```scala
// Replace by _id (reads _id from the value itself)
def replaceOne(value: A): Observable[UpdateResult]
def replaceOne(value: A, options: ReplaceOptions): Observable[UpdateResult]

// Replace with explicit filter
def replaceOne(filter: Bson, value: A): Observable[UpdateResult]
def replaceOne(filter: Bson, value: A, options: ReplaceOptions): Observable[UpdateResult]

// Field-level updates
def updateOne(filter: Bson, update: Bson): Observable[UpdateResult]
def updateOne(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult]

def updateMany(filter: Bson, update: Bson): Observable[UpdateResult]
def updateMany(filter: Bson, update: Bson, options: UpdateOptions): Observable[UpdateResult]
```

## Delete

```scala
def deleteOne(value: A): Observable[DeleteResult]
def deleteOne(filter: Bson): Observable[DeleteResult]
def deleteOne(filter: Bson, options: DeleteOptions): Observable[DeleteResult]

def deleteMany(filter: Bson): Observable[DeleteResult]
def deleteMany(filter: Bson, options: DeleteOptions): Observable[DeleteResult]

def deleteAll(): Observable[DeleteResult]
def deleteAll(options: DeleteOptions): Observable[DeleteResult]
```

## Atomic Find-and-Modify

For atomic read-modify-write semantics see the dedicated [Find-and-Modify](find-and-modify.md) page.

```scala
def findOneAndUpdate(filter: Bson, update: Bson): SingleObservable[A]
def findOneAndUpdate(filter: Bson, update: Bson, options: FindOneAndUpdateOptions): SingleObservable[A]

def findOneAndDelete(filter: Bson): SingleObservable[A]
def findOneAndDelete(filter: Bson, options: FindOneAndDeleteOptions): SingleObservable[A]

def findOneAndReplace(filter: Bson, replacement: A): SingleObservable[A]
def findOneAndReplace(filter: Bson, replacement: A, options: FindOneAndReplaceOptions): SingleObservable[A]

def upsertOne(filter: Bson, value: A): Observable[UpdateResult]
```

## Bulk Write

```scala
def bulkWrite(requests: List[WriteModel[Document]], ordered: Boolean = true): SingleObservable[BulkWriteResult]
def bulkWrite(requests: List[WriteModel[Document]], options: BulkWriteOptions): SingleObservable[BulkWriteResult]

def bulkWriteMany(values: Seq[A], ordered: Boolean = true): SingleObservable[BulkWriteResult]
def bulkWriteMany(values: Seq[A], options: BulkWriteOptions): SingleObservable[BulkWriteResult]
```

## Transaction Overloads

Every write method also accepts a `ClientSession` as the last argument so it can participate in a `withTransaction` block. See [Transactions](../database/transactions.md) for full details.

```scala
def insertOne(value: A, session: ClientSession): Observable[InsertOneResult]
def insertMany(values: Seq[A], session: ClientSession): Observable[InsertManyResult]
def replaceOne(filter: Bson, value: A, session: ClientSession): Observable[UpdateResult]
def updateOne(filter: Bson, update: Bson, session: ClientSession): Observable[UpdateResult]
def updateMany(filter: Bson, update: Bson, session: ClientSession): Observable[UpdateResult]
def deleteOne(filter: Bson, session: ClientSession): Observable[DeleteResult]
def deleteMany(filter: Bson, session: ClientSession): Observable[DeleteResult]
```
