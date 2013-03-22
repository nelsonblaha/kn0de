package models

import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.json.Json

import play.api.Logger

import scala.slick.driver.PostgresDriver.simple._

import java.util.Calendar
import java.sql.Date

import auth._

case class Sub(id: Option[Long] = None,
               name: String,
               description: String,
               createdBy: Long, // user id
               createdAt: Date = new Date(Calendar.getInstance().getTime().getTime()),
               totalMembers: Int = 0)

case class Moderator(accountId: Long,
					 subId: Long)                       

object Sub {
  
  def database = Database.forDataSource(DB.getDataSource())

  val SubTable = new Table[Sub]("sub") {
    def id = column[Long]("sub_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def createdBy = column[Long]("created_by")
    def createdAt = column[Date]("created_timestamp")
    def totalMembers = column[Int]("total_members")
    def * = id.? ~ name ~ description ~ createdBy ~ createdAt ~ totalMembers <> (Sub.apply _, Sub.unapply _)
    def forInsert = name ~ description ~ createdBy ~ createdAt ~ totalMembers <> 
      ({ s => Sub(None, s._1, s._2, s._3, s._4, s._5)}, { (s: Sub) => Some(s.name, s.description, s.createdBy, s.createdAt, s.totalMembers) })
  }

  def findById(id: Long): Option[Sub] = database.withSession { implicit db: Session =>
  	Query(SubTable).filter(a => a.id === id).firstOption
  }

  def findByName(name: String): Option[Sub] = database.withSession { implicit db: Session =>
  	Query(SubTable).filter(a => a.name === name).firstOption
  }

  def create(sub: Sub) = {
    // Create the sub in psql
    implicit val subId = database.withSession { implicit db: Session =>
      Logger.info("creating sub")
      SubTable.forInsert returning SubTable.id insert sub
    }

    // Create the initial subscription for the user creating the sub
    Subscription.create(sub.createdBy, subId, sub.name)
  }
 
  def frontpage(subId: Long): Option[Seq[(Item, Account)]] = database.withSession { implicit db: Session =>
    //Some(Query(Item.ItemTable).filter(s => s.postedTo === subId).list)
    Sub.findById(subId) match {
      case Some(sub) => Some((for {
          i <- Item.ItemTable if (i.postedTo === subId)
          a <- Account.AccountTable if (i.postedBy === a.id)
        } yield (i, a)).list)
      case _ => None
    }
  }

}

case class Subscription(accountId: Long,
                        subId: Long,
                        subName: String,
                        createdDate: Date = new java.sql.Date(Calendar.getInstance().getTime().getTime()))

object Subscription {

  def database = Database.forDataSource(DB.getDataSource())

  val SubscriptionTable = new Table[Subscription]("subscription") {
    def accountId = column[Long]("account_id")
    def subId = column[Long]("sub_id")
    def subName = column[String]("sub_name")
    def createdDate = column[Date]("created_date")
    def * = accountId ~ subId ~ subName ~ createdDate <> (Subscription.apply _, Subscription.unapply _)
  }

  def findByAccount(maybeUser: Option[Account]): List[Subscription] = database.withSession { implicit db: Session =>
    val accountId = for {
      account <- maybeUser
      accountId <- account.id
    } yield accountId

    accountId match {
      case Some(id) => Query(SubscriptionTable).filter(s => s.accountId === accountId).sortBy(_.createdDate.desc).list
      case _ => List[Subscription]()
    }
  }

  def create(userId: Long, subId: Long, subName: String) = database.withSession { implicit db: Session =>
    Logger.info("creating subscription for " + userId + " and " + subName)

    SubscriptionTable insert Subscription(userId, subId, subName)
  }

}

object Moderator {
  
  lazy val database = Database.forDataSource(DB.getDataSource())
  
  val ModeratorTable = new Table[Moderator]("moderator") {
    def accountId = column[Long]("account_id")
    def subId = column[Long]("sub_id")
    def * = accountId ~ subId <> (Moderator.apply _, Moderator.unapply _)
  }

  def findByAccount(accountId: Long): Seq[Moderator] = database.withSession { implicit db: Session =>
    Query(ModeratorTable).filter(s => s.accountId === accountId).list
  }
  
  def isMod(accountId: Long, subId: Long): Boolean = database.withSession { implicit db: Session =>
    Query(ModeratorTable).filter(m => m.accountId === accountId && m.subId === subId).firstOption.isDefined
  }

  def create(accountId: Long, subId: Long) = database.withSession { implicit db: Session =>
    Logger.info("creating subscription for " + accountId + " and " + subId)
    ModeratorTable.insert(Moderator(accountId, subId))
  }

}
