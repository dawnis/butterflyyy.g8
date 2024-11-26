package $organization$

import $organization$.migrations.FlyyDb
import $organization$.models.*
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*

//#set-up
class FlywayDbMigrationSpec extends AnyWordSpec with Matchers with BeforeAndAfter {
  //#test-top

  after {
    val dbConnection = DatabaseConnection("h2mem1")
    dbConnection.close()
  }

  "FlywayMigration" should {
    "create users table" in {
      val tb: TableQuery[UserTable] = TableQuery[UserTable]

//      val setupAction: DBIO[Unit] = DBIO.seq(
//        tb.schema.create,
//        tb += UserActor(None, "Bob", 21, Some("Sarasota, Florida"))
//      )

      val dbConnection = DatabaseConnection("h2mem1")
      val db = dbConnection.db

//      val setupFuture: Future[Unit] = db.run(setupAction)
//
//      Await.result(setupFuture, Duration.Inf)

      val flywayMigrator = FlyyDb(dbConnection)
      flywayMigrator.migrate()

      val check_flyway_history_query = sql"""SELECT "script", "description", "success" FROM "flyway_schema_history"""".as[(String, String, Int)]

      val flywayResult = db.run(check_flyway_history_query)
      println(Await.result(flywayResult, Duration.Inf))


      val addUserFuture: DBIO[Unit] = DBIO.seq(
        tb += UserActor(None, "Bob", 21, Some("Sarasota, Florida"))
      )

      Await.result(db.run(addUserFuture), Duration.Inf)

      val selectStar = tb.result
      // Print the SQL for the filter query
      println("Generated SQL for star query:\n" + selectStar.statements.mkString)

      // This query does not work for unknown reasons
      val bob: Rep[String] = LiteralColumn("Bob")
      val bobQuery = tb.filter(_.name === bob)
      println("Generated SQL for filter query:\n" + bobQuery.result.statements.mkString)

      val userFuture: Future[Seq[UserActor]] = db.run(selectStar)
      val userFetched: Seq[UserActor] = Await.result(userFuture, Duration.Inf)

      userFetched.head shouldBe UserActor(Some(1), "Bob", 21, Some("Sarasota, Florida"))

    }
  }

}
