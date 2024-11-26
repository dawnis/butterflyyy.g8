package $organization$.models

import slick.jdbc.JdbcBackend.Database
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

//TODO: turn this into a class which can connect to either postgres or h2mem (for testing)
//TODO: make Hikari Datasource available

class DatabaseConnection(configMode: String) {

  //expected values are h2mem1 or db, see application.conf
  val db = Database.forConfig(configMode)

  // Access the underlying HikariDataSource
  val hikariDataSource = db.source match {
    case ds: HikariDataSource => ds
    case managed: slick.jdbc.hikaricp.HikariCPJdbcDataSource => managed.ds
    case _ => throw new Exception("The database source is not HikariCP")
  }


  def close(): Unit = db.close()
}

object DatabaseConnection {
  def apply(configMode: String) = new DatabaseConnection(configMode)
}
