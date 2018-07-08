package com.sfxcode.nosql.mongo.gridfs

import com.sfxcode.nosql.mongo.GridFSDAO
import com.sfxcode.nosql.mongo.database.DatabaseProvider

/**
 * GridFS Database Sample
 */
object GridfsDatabase {

  val database = DatabaseProvider("test")

  object ImageFilesDAO extends GridFSDAO(database, "images")

}

