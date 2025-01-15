package $organization$.actors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.collection.immutable

import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database
import slick.lifted.ProvenShape

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef

import $organization$.models.*

object UserActors {
  sealed trait Command
  final case class GetAllUsers(replyTo: ActorRef[UserActorSeq]) extends Command
  final case class GetUser(name: String, replyTo: ActorRef[GetUserResponse]) extends Command
  final case class CreateUser(user: UserActor, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class DeleteUser(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetUserResponse(maybeUser: Option[UserActor])
  final case class ActionPerformed(description: String)

  def apply(dbConnectionType: String): Behavior[Command] = {
        val db: Database = Database.forConfig(dbConnectionType)
        new UserActors(db).tb_request
  }
}

class UserActors(dbConnection: Database) {

  private val table = TableQuery[UserTable]
  private def getAllUsers(): Future[Seq[UserActor]] = dbConnection.run(table.result)
  private def getUser(name: String): Future[UserActor] = {
    val userQuery = table.filter(_.name === name)
    dbConnection.run (
      userQuery.result.head
    )
  }
  private def createUser(user: UserActor) = dbConnection.run(
    table += user
  )
  private def deleteUser(name: String) = {
    val deleteQuery = table.filter(_.name === name)
    dbConnection.run (
      deleteQuery.delete
    )
  }


  // Actor message handling
  private def tb_request: Behavior[UserActors.Command] = Behaviors.receiveMessage {
    case UserActors.GetAllUsers(replyTo) =>
      getAllUsers().onComplete {
        case Success(u) => replyTo ! UserActorSeq(u)
        case Failure(e) =>  e.printStackTrace()
      }
      Behaviors.same
    case UserActors.GetUser(name,replyTo) =>
      getUser(name).onComplete {
        case Success(u) => replyTo ! UserActors.GetUserResponse(Some(u))
        case Failure(e) =>  replyTo ! UserActors.GetUserResponse(None)

      }
      Behaviors.same
    case UserActors.CreateUser(user, replyTo) =>
      replyTo ! UserActors.ActionPerformed(s"User \${user.name} created.")
      createUser(user)
      Behaviors.same
    case UserActors.DeleteUser(name, replyTo) =>
      replyTo ! UserActors.ActionPerformed(s"User \$name deleted.")
      deleteUser(name)
      Behaviors.same
  }
}

