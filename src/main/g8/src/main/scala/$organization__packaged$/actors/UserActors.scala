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

  def apply(): Behavior[Command] = tb_request

  val db: Database = Database.forConfig("h2mem1")

  private val table = TableQuery[UserTable]
  private def getAllUsers(): Future[Seq[UserActor]] = db.run(table.result)
  private def getUser(name: String): Future[UserActor] = {
    val userQuery = table.filter(_.name === name)
    db.run (
      userQuery.result.head
    )
  }
  private def createUser(user: UserActor) = db.run(
    table += user
  )
  private def deleteUser(name: String) = {
    val deleteQuery = table.filter(_.name === name)
    db.run (
      deleteQuery.delete
    )
  }


  // Actor message handling
  private def tb_request: Behavior[Command] = Behaviors.receiveMessage {
    case GetAllUsers(replyTo) =>
      getAllUsers().onComplete {
        case Success(u) => replyTo ! UserActorSeq(u)
        case Failure(e) =>  e.printStackTrace()
      }
      Behaviors.same
    case GetUser(name,replyTo) =>
      getUser(name).onComplete {
        case Success(u) => replyTo ! GetUserResponse(Some(u))
        case Failure(e) =>  replyTo ! GetUserResponse(None)

      }
      Behaviors.same
    case CreateUser(user, replyTo) =>
      replyTo ! ActionPerformed(s"User \${user.name} created.")
      createUser(user)
      Behaviors.same
    case DeleteUser(name, replyTo) =>
      replyTo ! ActionPerformed(s"User \$name deleted.")
      deleteUser(name)
      Behaviors.same
  }
}

