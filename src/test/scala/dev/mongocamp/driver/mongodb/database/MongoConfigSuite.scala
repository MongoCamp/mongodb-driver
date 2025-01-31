package dev.mongocamp.driver.mongodb.database

import munit.FunSuite

class MongoConfigSuite extends FunSuite {

  test("MongoConfig should be created by database name") {
    val config = MongoConfig("config_test")
    assertEquals(config.database, "config_test")
    val shortDescription = "{hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
    assertEquals(config.clientSettings.getClusterSettings.getShortDescription, shortDescription)
    assertEquals(config.clientSettings.getApplicationName, "mongocampdb-app")
    assertEquals(config.clientSettings.getClusterSettings.getHosts.size(), 1)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMinSize, 0)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMaxSize, 50)
  }

  test("MongoConfig should be created with Properties") {
    val config = MongoConfig("config_test", host = "localhost", applicationName = "Awesome Application Name")
    assertEquals(config.database, "config_test")
    val shortDescription = "{hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
    assertEquals(config.clientSettings.getClusterSettings.getShortDescription, shortDescription)
    assertEquals(config.clientSettings.getApplicationName, "Awesome Application Name")
    assertEquals(config.clientSettings.getClusterSettings.getHosts.size(), 1)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMinSize, 0)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMaxSize, 50)
  }

  test("MongoConfig should be created by config") {
    val config = MongoConfig.fromPath("config.test.mongo")
    assertEquals(config.database, "mongocamp-unit-test")
    val shortDescription = "{hosts=[localhost:270007], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
    assertEquals(config.clientSettings.getClusterSettings.getShortDescription, shortDescription)
    assertEquals(config.clientSettings.getApplicationName, "mongocamp-config-test")
    assertEquals(config.clientSettings.getClusterSettings.getHosts.size(), 1)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMinSize, 5)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMaxSize, 100)
    assertEquals(Option(config.clientSettings.getCredential), None)
  }

  test("MongoConfig should be created by config with auth") {
    val config = MongoConfig.fromPath("config.test.auth.mongo")
    assertEquals(config.database, "mongocamp-unit-test")
    val shortDescription = "{hosts=[localhost:270007], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}"
    assertEquals(config.clientSettings.getClusterSettings.getShortDescription, shortDescription)
    assertEquals(config.clientSettings.getApplicationName, "mongocamp-config-test-with-auth")
    assertEquals(config.clientSettings.getClusterSettings.getHosts.size(), 1)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMinSize, 5)
    assertEquals(config.clientSettings.getConnectionPoolSettings.getMaxSize, 100)
    assertEquals(config.clientSettings.getCredential.getUserName, "admin_user")
    assertNotEquals(Option(config.clientSettings.getCredential.getPassword), None)
    assertNotEquals(config.clientSettings.getCredential.getPassword.length, 0)
  }
}
