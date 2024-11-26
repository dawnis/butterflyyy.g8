package $organization$
import $organization$.migrations.FlyyDb
import $organization$.models.DatabaseConnection

object FlywayMigration extends App {
  val dbConnection = DatabaseConnection("db")
  val flywayMigrator = FlyyDb(dbConnection)
  flywayMigrator.migrate()
}
