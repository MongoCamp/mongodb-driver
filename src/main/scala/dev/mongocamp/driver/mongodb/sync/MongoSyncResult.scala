package dev.mongocamp.driver.mongodb.sync

import java.util.Date

case class MongoSyncResult(
    collectionName: String,
    syncDate: Date = new Date(),
    acknowleged: Boolean = false,
    synced: Int = -1,
    countBefore: Int = -1,
    countAfter: Int = -1,
    syncTime: Long = -1,
    exception: Option[Exception] = None
)
