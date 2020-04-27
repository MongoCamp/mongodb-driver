# DatabaseProvider

DatabaseProvider is the central repository for MongoClient, registries, databases and collections.

DatabaseProvider gives access to

* MongoClient
* MongoDatabase
* MongoCollection

## Registries

@@@ note { title=ScalaDriverDocs }

Additional Info for [Registries](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/getting-started/quick-start-case-class/#configuring-case-classes)

@@@

### Create Case Classes

@@snip [Scala Sources](/docs/src/main/scala/DatabaseProviderDoc.scala) { #provider_with_registry_classes }

### Create Registry

@@snip [Scala Sources](/docs/src/main/scala/DatabaseProviderDoc.scala) { #provider_with_registry }

## Multiple databases access





