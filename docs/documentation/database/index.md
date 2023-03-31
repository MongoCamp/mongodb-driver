# Database

Database Connection can be done using MongConfig.

DatabaseProvider Instance contains setup for registries and databases.

Database provider will ues database by name from MongoConfig by default.
Multiple databases access is supported on the same client is supported. For different MongoDBs you have to use different providers.

## DatabaseProvider
DatabaseProvider is the central repository for MongoClient, registries, databases and collections.

Every [Mongo DAO](../mongo-dao/index.md) / [GridFs DAO](../gridfs-dao/index.md) Instance needs this class.

```scala
  val provider: DatabaseProvider = DatabaseProvider(MongoConfig.fromPath())

  val database: MongoDatabase = provider.database()

  // Infos for all collections in the default database
  val collectionInfos: List[CollectionInfo] = provider.collectionInfos()
```