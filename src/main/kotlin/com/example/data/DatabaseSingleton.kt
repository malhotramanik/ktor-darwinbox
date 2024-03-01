package com.example.data

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSingleton {
  fun init() {
//    val driverClassName = "org.h2.Driver"
//    val jdbcURL = "jdbc:h2:file:./build/db"
//    val database = Database.connect(jdbcURL, driverClassName)

    val driverClassName = "org.postgresql.Driver"

    // Define the JDBC URL for the PostgreSQL database
    val jdbcURL = "jdbc:postgresql://localhost:5432/employee_db"
    val username = "postgres"
    val password = ""

    // Connect to the PostgreSQL database using the driver class name, JDBC URL, username, and password
    val database = Database.connect(jdbcURL, driverClassName, user = username, password = password)

    transaction(database) {
      SchemaUtils.create(Employee)
    }
  }

  suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
}
