package com.sfxcode.nosql.mongo.database

import org.specs2.mutable.Specification

class MongoConfigSpec extends Specification {

  sequential

  "MongoConfig" should {

    "be created by database name " in {
      val config = MongoConfig("config_test")
      config.database must beEqualTo("config_test")
      val shortDescription =
        "{hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
      config.clientSettings.getClusterSettings.getShortDescription must be equalTo shortDescription
      config.clientSettings.getApplicationName must be equalTo "simple-mongo-app"
      config.clientSettings.getClusterSettings.getHosts.size() must be equalTo 1
      config.clientSettings.getConnectionPoolSettings.getMinSize must be equalTo 0
      config.clientSettings.getConnectionPoolSettings.getMaxSize must be equalTo 50
    }

    "be created with Properties " in {

      val config = MongoConfig("config_test", host = "localhost", applicationName = "Awesome Application Name")

      config.database must beEqualTo("config_test")
      val shortDescription =
        "{hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
      config.clientSettings.getClusterSettings.getShortDescription must be equalTo shortDescription
      config.clientSettings.getApplicationName must be equalTo "Awesome Application Name"
      config.clientSettings.getClusterSettings.getHosts.size() must be equalTo 1
      config.clientSettings.getConnectionPoolSettings.getMinSize must be equalTo 0
      config.clientSettings.getConnectionPoolSettings.getMaxSize must be equalTo 50
    }

    "be created by config " in {
      val config = MongoConfig.fromPath("config.test.mongo")
      config.database must beEqualTo("config_path_test")
      val shortDescription =
        "{hosts=[localhost:270007], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
      config.clientSettings.getClusterSettings.getShortDescription must be equalTo shortDescription
      config.clientSettings.getApplicationName must be equalTo "simple-mongo-config-test"
      config.clientSettings.getClusterSettings.getHosts.size() must be equalTo 1
      config.clientSettings.getConnectionPoolSettings.getMinSize must be equalTo 5
      config.clientSettings.getConnectionPoolSettings.getMaxSize must be equalTo 100
      config.clientSettings.getCredential must beNull

    }

    "be created by config with auth " in {
      val config = MongoConfig.fromPath("config.test.auth.mongo")
      config.database must beEqualTo("config_path_test")
      val shortDescription =
        "{hosts=[localhost:270007], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
      config.clientSettings.getClusterSettings.getShortDescription must be equalTo shortDescription
      config.clientSettings.getApplicationName must be equalTo "simple-mongo-config-test-with-auth"
      config.clientSettings.getClusterSettings.getHosts.size() must be equalTo 1
      config.clientSettings.getConnectionPoolSettings.getMinSize must be equalTo 5
      config.clientSettings.getConnectionPoolSettings.getMaxSize must be equalTo 100
      config.clientSettings.getCredential.getUserName must be equalTo "admin_user"
      config.clientSettings.getCredential.getPassword must not beEmpty

    }

  }

}
