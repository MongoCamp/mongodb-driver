# DatabaseProvider

DatabaseProvider is the central repository for MongoClient, databases, collections and DocumentDAO`s.

DatabaseProvider gives access to

* MongoClient
* MongoDatabase
* MongoCollection

## DocumentDAO 
From an DocumentDAO you can perform DocumentDAO initialization and caching. On this DAO you can perform CRUD operations.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/database/DatabaseProviderSuite.scala#document-dao

## ~~Registries~~

::: danger 
Registries are no longer supported for automatic case class conversion. For scala 3 support we changed from mongodb driver conversion to circe conversion.
:::