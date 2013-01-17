package models

import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.json.Json

import play.api.Logger

import anorm._
import anorm.SqlParser._

import java.util.{ Calendar, Date }

import auth._

case class Sub(id: Pk[Long] = NotAssigned,
               name: String,
               description: String,
               createdBy: Long, // user id
               createdAt: Date = Calendar.getInstance().getTime(),
               totalMembers: Int = 0)

case class Subscription(accountId: Long,
                        subId: Long)

object Sub {

  def simple = {
    get[Pk[Long]]("sub.sub_id") ~
    get[String]("sub.name") ~
    get[String]("sub.description") ~
    get[Long]("sub.created_by") ~
    get[Date]("sub.created_at") ~
    get[Int]("sub.total_members") map {
      case id~name~description~createdBy~createdAt~totalMembers =>
      Sub(id, name, description, createdBy, createdAt, totalMembers)
    }
  }

  def defaults = Seq[Sub]()

  def findById(id: Long): Option[Sub] = {
    DB.withConnection { implicit connection =>
      SQL("select * from sub where sub_id = {id}").on(
        'id -> id
      ).as(Sub.simple.singleOpt)
    }
  }

  def findByName(name: Long): Option[Sub] = {
    DB.withConnection { implicit connection =>
      SQL("select * from sub where name = {name}").on(
        'name -> name
      ).as(Sub.simple.singleOpt)
    }
  }

  def isMod(subId: Long, accountId: Long): Boolean = {
    DB.withConnection { implicit connection =>
      SQL("""
        select count(moderator.sub_id) = 1 from moderator
        where moderator.sub_id = {sub_id} and moderator.account_id = {account_id}
        """).on(
        'sub_id -> subId,
        'account_id -> accountId
      ).as(scalar[Boolean].single)
    }
  }

  def create(sub: Sub): Option[Long] = {
    Logger.info("creating sub")
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO sub VALUES (DEFAULT, {name}, {desc}, {created_by}, DEFAULT, DEFAULT)").on(
        'name -> sub.name,
        'desc -> sub.description,
        'created_by -> sub.createdBy
      ).executeInsert()
    }
  }

  def frontpage = Nil

}

object Subscription   {

  val simple = {
    get[Long]("subscription.account_id") ~
    get[Long]("subscription.sub_id") map {
      case accountId~subId => Subscription(accountId, subId)
    }
  }

  def findByAccount(accountId: Long): Seq[Subscription] = {
    DB.withConnection { implicit connection =>
      SQL("select * from subscription where subscription.account_id = {account_id}").on(
        'account_id -> accountId
      ).as(Subscription.simple *)
    }
  }

  def create(accountId: Long, subId: Long) = {
    Logger.info("creating subscription for " + accountId + " and " + subId)
    DB.withConnection { implicit connection =>
      SQL("insert into subscription values ({account_id}, {sub_id}, DEFAULT)").on(
        'account_id -> accountId,
        'sub_id -> subId
      ).executeInsert()
    }
  }

}
