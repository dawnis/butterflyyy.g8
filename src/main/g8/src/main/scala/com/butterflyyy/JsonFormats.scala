package com.butterflyyy
//import com.butterflyyy.examples.{User, Users}
//import com.butterflyyy.examples.UserRegistry.ActionPerformed

//#json-formats
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol

import com.butterflyyy.actors.*
import com.butterflyyy.models.*

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

//  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat3(User.apply)
//  implicit val usersJsonFormat: RootJsonFormat[Users] = jsonFormat1(Users.apply)
//
//  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed]  = jsonFormat1(ActionPerformed.apply)

  implicit val userActorJsonFormat: RootJsonFormat[UserActor] = jsonFormat4(UserActor.apply)
  implicit val userActorsJsonFormat: RootJsonFormat[UserActorSeq] = jsonFormat1(UserActorSeq.apply)

  implicit val actionActorPerformedJsonFormat: RootJsonFormat[UserActors.ActionPerformed] = jsonFormat1(UserActors.ActionPerformed.apply)


}
//#json-formats
