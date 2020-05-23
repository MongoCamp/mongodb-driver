# Database

Database Connection can be done using MongConfig.

DatabaseProvider Instance contains setup for registries and databases.

Database provider will ues database by name from MongoConfig by default.
Multiple databases access is supported on the same client is supported. For different MongoDBs you have to use different providers.

## DatabaseProvider
DatabaseProvider is the central repository for MongoClient, registries, databases and collections.

Every @ref:[index](../dao/index.md) / @ref:[index](../gridfs/index.md) Instance needs this class.

@@snip [Scala Sources](/docs/src/main/scala/DatabaseProviderDoc.scala) { #provider }


@@@@ index

- [config](config.md)
- [provider](provider.md)
- [reactive_streams](reactive_streams.md)
- [bson](bson.md)
- [relationships](relationships.md)

@@@@

