package $organization$.migrations

import $organization$.models.DatabaseConnection
import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

class FlyyDb(db: DatabaseConnection) {

  private val config = ConfigFactory.load()
  private val flywayConfig = config.getConfig("flyway")
  
  // Configure Flyway to use the data source and locations defined in the config
  val flyway = Flyway.configure()
    .dataSource(db.hikariDataSource) // Use the Slick/Hikari data source
    .locations(flywayConfig.getString("locations")) // Get migration locations
    .load()

  def migrate(): Unit = {
    try {
      flyway.migrate()
    }  catch {
      case e: Exception =>
        println(s"Migration failed: \${e.getMessage}")
    }
    

  }

}

object FlyyDb{
  def apply(db: DatabaseConnection) = new FlyyDb(db)
}
