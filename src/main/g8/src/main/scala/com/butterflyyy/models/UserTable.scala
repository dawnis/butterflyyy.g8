package com.butterflyyy.models

import slick.lifted.ProvenShape
import scala.collection.immutable
import slick.jdbc.H2Profile.api._

case class UserActor(id: Option[Long], name: String, age: Int, country: Option[String])
case class UserActorSeq(users: immutable.Seq[UserActor])

//Note that for Postgres -- Table[UserActor](tag, Some("public"), _tableName="users") would specify public schema
class UserTable(tag: Tag) extends Table[UserActor](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def age = column[Int]("age")
  def country = column[Option[String]]("country")
  //Some alternative ways of linking to the case class to default projection
  //  override def * = (name, age, country) <> ((UserActor.apply _).tupled, UserActor.unapply)
  override def * : ProvenShape[UserActor] = (id.?, name, age, country).mapTo[UserActor]
}
