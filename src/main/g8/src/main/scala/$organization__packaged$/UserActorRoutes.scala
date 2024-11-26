package $organization$

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import $organization$.actors.*
import $organization$.models.*

import scala.concurrent.Future

//#import-json-formats
//#user-routes-class
class UserActorRoutes(userRegistry: ActorRef[UserActors.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  // json-formats


  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getUsers(): Future[UserActorSeq] =
    userRegistry.ask(UserActors.GetAllUsers.apply)
  def getUser(name: String): Future[UserActors.GetUserResponse] =
    userRegistry.ask(UserActors.GetUser(name, _))
  def createUser(user: UserActor): Future[UserActors.ActionPerformed] =
    userRegistry.ask(UserActors.CreateUser(user, _))
  def deleteUser(name: String): Future[UserActors.ActionPerformed] =
    userRegistry.ask(UserActors.DeleteUser(name, _))

  val userRoutes: Route =
    pathPrefix("users") {
      pathEnd {
        // Get All Users
        get {
          complete(getUsers())
        } ~
        // post-create-user
          post {
            entity(as[UserActor]) { user =>
              onSuccess(createUser(user)) { performed =>
                complete(StatusCodes.Created, performed)

              }
            }
          }
      } ~
      path(Segment) { name =>
        //get-retrieve-user
        get {
          //#retrieve-user-info
          rejectEmptyResponse {
            onSuccess(getUser(name)) { response =>
              complete(response.maybeUser)
            }
          }
          //#retrieve-user-info
        } ~
        //delete-user by name
        delete {
          onSuccess(deleteUser(name)) { performed =>
            complete((StatusCodes.OK, performed))
          }
        }
      }
    }
}
