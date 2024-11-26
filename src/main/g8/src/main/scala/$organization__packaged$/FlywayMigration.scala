package com.butterflyyy
import com.butterflyyy.migrations.FlyyDb
import com.butterflyyy.models.DatabaseConnection

object FlywayMigration extends App {
  val dbConnection = DatabaseConnection("db")
  val flywayMigrator = FlyyDb(dbConnection)
  flywayMigrator.migrate()
}
