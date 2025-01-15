package $organization$

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.util.Failure
import scala.util.Success

// Hello Slick Tutorial Setup
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
// Use H2Profile to connect to an H2 database
import slick.jdbc.H2Profile.api._


import $organization$.models.*
import $organization$.actors.*

//#main-class
object ButterflyyApp {
  //#start-http-server
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  //#start-http-server
  def main(args: Array[String]): Unit = {
    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val userRegistryActor = context.spawn(UserActors("h2mem1"), "UserRegistryActor")
      context.watch(userRegistryActor)

      val routes = new UserActorRoutes(userRegistryActor)(context.system)
      startHttpServer(routes.userRoutes)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")

//    val db = DatabaseConnection.db
//    val users: TableQuery[UserTable] = TableQuery[UserTable]
//    val setupFuture: Future[Unit] = db.run(
//      users.schema.create
//    )
//    Await.result(setupFuture, Duration.Inf)

  }
}
//#main-class
