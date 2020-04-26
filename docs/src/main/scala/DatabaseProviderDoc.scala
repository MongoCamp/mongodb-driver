import com.sfxcode.nosql.mongo.database.{DatabaseProvider, MongoConfig}
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{MongoDatabase, Observable}

// #provider_with_registry_classes
case class Student(_id: Long, name: String, scores: List[Score])

case class Score(score: Double, `type`: String)

case class Grade(_id: ObjectId, student_id: Long, class_id: Long, scores: List[Score])
// #provider_with_registry_classes

object DatabaseProviderDoc {

  // #sample_config_from_path
  val config: MongoConfig = MongoConfig.fromPath()
  // #sample_config_from_path

  // #sample_config_from_custom_path
  val customConfig: MongoConfig = MongoConfig.fromPath("mongo.db.prod")
  // #sample_config_from_custom_path

  // #provider
  val provider: DatabaseProvider = DatabaseProvider(MongoConfig.fromPath())

  val database: MongoDatabase = provider.database()

  val databaseNames: Observable[String]   = provider.databaseNames()
  val collectionNames: Observable[String] = provider.collectionNames()
  // #provider

  // #provider_with_registry
  val registry: CodecRegistry =
    fromProviders(classOf[Student], classOf[Score], classOf[Grade])

  val providerWithRegistry: DatabaseProvider =
    DatabaseProvider(MongoConfig.fromPath(), registry)
  // #provider_with_registry
}
