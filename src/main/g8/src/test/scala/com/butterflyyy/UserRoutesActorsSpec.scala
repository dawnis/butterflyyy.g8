package com.butterflyyy

import com.butterflyyy.actors.*
import com.butterflyyy.models.*

//#user-routes-spec
//#test-top
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.*
import akka.http.scaladsl.testkit.ScalatestRouteTest

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.BeforeAndAfter

import slick.jdbc.H2Profile.api.*
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

//#set-up
class UserRoutesActorsSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest with BeforeAndAfter {
  //#test-top

  // the Akka HTTP route testkit does not yet support a typed actor system (https://github.com/akka/akka-http/issues/2036)
  // so we have to adapt for now
  lazy val testKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[_] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  // Here we need to implement all the abstract members of UserRoutes.
  // We use the real UserRegistryActor to test it while we hit the Routes,
  // but we could "mock" it by implementing it in-place or by using a TestProbe
  // created with testKit.createTestProbe()
  val userRegistry = testKit.spawn(UserActors())
  lazy val routes = new UserActorRoutes(userRegistry).userRoutes

  // use the json formats to marshal and unmarshall objects in the test
  import JsonFormats.*
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
  //#set-up

  before {
    val db = Database.forConfig("h2mem1")
    val users: TableQuery[UserTable] = TableQuery[UserTable]
    val setupFuture: Future[Unit] = db.run(
      users.schema.create
    )
    Await.result(setupFuture, Duration.Inf)
  }

  after {
    val db = Database.forConfig("h2mem1")
    db.close
  }

  //#actual-test
  "UserRoutes" should {
    "return no users if no present (GET /users)" in {
      // note that there's no need for the host part in the uri:
      val request = HttpRequest(uri = "/users")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"users":[]}""")
      }
    }
    //#actual-test

    //#testing-post
    "be able to add users (POST /users)" in {
      val user = UserActor(None, "Kapi", 42, Some("jp"))
      val userEntity = Marshal(user).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      // using the RequestBuilding DSL:
      val request = Post("/users").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and we know what message we're expecting back:
        entityAs[String] should ===("""{"description":"User Kapi created."}""")
      }
    }
    //#testing-post

    "be able to remove users (DELETE /users)" in {
      // user the RequestBuilding DSL provided by ScalatestRouteSpec:
      val request = Delete(uri = "/users/Kapi")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"description":"User Kapi deleted."}""")
      }
    }
    //#actual-test
  }
  //#actual-test

  //#set-up
}
//#set-up
//#user-routes-spec
