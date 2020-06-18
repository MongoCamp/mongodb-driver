# Changes #


## Versions

### 2.0.3
* Bugfix CollectionStatus

### 2.0.5
* mongo-scala-driver 4.0.4
* dependency updates

### 2.0.2
* mongo-scala-driver 4.0.3
* dependency updates
* Sync for collections between databases added

### 2.0.1
* Mongo Java Server Support
* LocalServer, ServerConfig added

### 2.0.0
* mongo package no longer extends MongoImplcits => Use MongoImplcits trait if needed

### 1.9.7
* Support for Dot notation in documents
* extend Document functions with getValue. updateValue, getIntValue ... (with dot support)

### 1.9.4
* Rewrite Docs and Samples
* Specs Setup Redesign
* Added more Specs (Coverage min from 60 to 70)

### 1.9.2
* scala 2.13.2
* Minor Fixes

### 1.9.1
* database provider: runCommand added
* database provider: addChangeObserver added
* DAO changeObserver added
* DAO stats (GridFSDAO fileStats, chunkStats) as case class added

### 1.9.0

* ObservableIncludes changed method names for better understanding =>
  - result (A), results (Seq[A]), resultOption (Option[A]), resultList (List[A])
  - **Breaking Change** : dropped => headResult (use now result)
  - **Breaking Change** :  changed => result (old Option[A], new A) => use resultOption instead
* Add Bulkwrite to Crud Operations
* MongoDAO supports Json Import from File

### 1.8.2

* GridFSStreamObserver suports now completed and resultLength
* implcit resultLength (in bytes) for GridFSStreamObserver

### 1.8.0

* mongo-scala-driver 4.0.2
* maxWaitQueueSize is removed (since mongo-scala-driver 4.0.0)
* GridFS Refactoring (Download / Upload use now reactive streams)
* More Index Functions added

### 1.7.1
* [mongo-scala-driver 2.9.0](https://mongodb.github.io/mongo-scala-driver/2.9/changelog/)


### 1.7.0
* [mongo-scala-driver 2.8.0](https://mongodb.github.io/mongo-scala-driver/2.8/changelog/)
* MongoDB Driver Async 3.12.0
* 
### 1.6.7
* Typesave Config 1.4.0

### 1.6.6
* Scala 2.13
* mongo-scala-driver 2.7.0
* MongoDB Driver Async 3.11.0

### 1.6.5
* MongoConfig added (also for config values in application.conf)
* MongoPoolOptions added
* Codecs prepared for Scala 2.13
* Additional index functions added
* Drop Scala 2.11 support
* Docs updated

### 1.6.2
* mongo-scala-driver 2.6.0
* MongoDB Driver Async 3.10.0
* sbt.version = 1.2.8

### 1.6.1
* sbt.version = 1.2.7

### 1.6.0

* mongo-scala-driver 2.5.0
* scalafmt support
* scalacheck support

### 1.5.6

* mongo-scala-driver 2.4.2

### 1.5.5

* mongo-scala-driver 2.4.1

### 1.5.3

* scala 2.11.12
* BsonConverter Char support


### 1.5.2

* GridFSDAO extends Metadata
* Metadata - updateMetadata added
* Metadata - updateMetadataElement/s added

### 1.5.1

* DatabaseProvider refactored
* ObservableImplicits to package object added
* update docs for Observable

### 1.5.0
* GridFSSupport


### 1.4.0
*  dropped operations with pattern <name>Result (use result, list result or implicit conversion instead)
*  introduce Database Functions trait
*  update docs


### 1.3.1
* update mongo-scala-driver to 2.4.0
* Operations Base count method support CountOptions


### 1.3.0

* Crud refactoring
* CrudObserver Trait (optional usage)


### 1.2.0

* Relations added
* Field added (used in Aggregation)
* Aggregation Specs added
* implicit Document(s) to Map(s) Conversion
* method sort in Sort object renamed to sortByKey

### 1.1.0

* update mongo-scala-driver to 2.3.0
* update to Scala 2.12.6

### 1.0.5

* update to Scala 2.12.5

### 1.0.4

* update to mongo scala driver 2.2.1

### 1.0.3

* gh-pages added
* test fixes

### 1.0.2

* update to mongo scala driver 2.2.0

### 1.0.1

* BuildInfo added
* dependencies updated
* plugins updated
* Index support addded

