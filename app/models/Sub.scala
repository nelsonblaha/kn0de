package models

import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.json.Json

import play.api.Logger

import scala.slick.driver.PostgresDriver.simple._

import java.util.Calendar
import java.sql.Date

import com.redis._
import controllers.RedisClients

import auth._

case class Sub(id: Option[Long] = None,
               name: String,
               description: String,
               createdBy: Long, // user id
               createdAt: Date = new java.sql.Date(Calendar.getInstance().getTime().getTime()),
               totalMembers: Int = 0)

case class Moderator(accountId: Long,
					 subId: Long)                       

object Sub extends RedisKeys {
  
  def database = Database.forDataSource(DB.getDataSource())
  lazy val redisClients = RedisClients.clientPool

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

    redisClients.withClient { client =>
      // Set up the nextItemId counter for the new sub
      client.set(nextItemId,0)

      // Set the sub hash
      client.hmset(subHash, Map(
        SubHash.subName -> sub.name,
        SubHash.subDescription -> sub.description,
        SubHash.createdBy -> sub.createdBy,
        SubHash.createdAt -> sub.createdAt
      ))
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

case class Subscription(name: String)

object Subscription extends RedisKeys  {
  
  lazy val redisClients = RedisClients.clientPool

  def findByAccount(maybeUser: Option[Account]): Set[Subscription] = redisClients.withClient { client =>
    implicit val userId = maybeUser.flatMap { _.id } getOrElse -1L
    client.smembers(subscriptionsForUser) map { _.flatten.map { Subscription(_) } }  getOrElse Set[Subscription]() 
  }

  def defaults(maybeUser: Option[Account]): Set[Subscription] = redisClients.withClient { client =>
    // Deprecated
    // Going to replace this with setting defaults at user-creation rather than dynamically
    // Auto-sub users to defaults, allow them to unsub later
    val userId = maybeUser.flatMap { _.id } getOrElse -1
    client.sdiff("subscriptions:1", s"subscriptions:$userId")
      .map { _.flatten.map { Subscription(_) } }  getOrElse Set[Subscription]()
  }

  def create(userId: Long, subId: Long, subName: String) = {
    Logger.info("creating subscription for " + userId + " and " + subName)

    redisClients.withClient { client =>
      // Add sub name to user's set of subscriptions
      client.sadd(subscriptionsForUser(userId), subName)

      // Add user ID to set of sub's subscribers
      client.sadd(subscribersForSub(subId), userId)
    }
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
