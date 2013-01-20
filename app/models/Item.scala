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

case class Item(id: Option[Long] = None,
                title: String,
                postedBy: Long, // user id
                postedTo: Long, // sub id
                score: Double,
                link: String,
                content: String,
                postedAt: Date = new Date(Calendar.getInstance().getTime().getTime())) // timestamp
  
object Item {

  lazy val database = Database.forDataSource(DB.getDataSource())
  
  val ItemTable = new Table[Item]("item") {
    def id = column[Long]("item_id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def postedBy = column[Long]("posted_by")
    def postedTo = column[Long]("posted_to")
    def score = column[Double]("score")
    def link = column[String]("link")
    def content = column[String]("content")
    def postedAt = column[Date]("posted_at")
    def * = id.? ~ title ~ postedBy ~ postedTo ~ score ~ link ~ content ~ postedAt <> (Item.apply _, Item.unapply _)
  }
  
  def findAllBySub(subId: Long): Seq[Item] = database.withSession { implicit db: Session =>
    Query(ItemTable).filter(i => i.postedTo === subId).list
  }
  
  def totalNumberItems: Long = database.withSession { implicit db: Session =>
    Query(ItemTable.length).first
  }

  def frontpage: Seq[(Item, Account)] = {
    Nil
  }

  def create(item: Item) = database.withSession { implicit db: Session =>
    Logger.info("creating new item")
    ItemTable.insert(item)
  }

}
