package dev.mongocamp.driver.mongodb.jdbc

import java.sql.SQLException

class SQLAlreadyClosedException(name: String) extends SQLException(name + " has already been closed.")