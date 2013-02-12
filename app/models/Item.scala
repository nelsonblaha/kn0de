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
                link: Option[String],
                content: String,
                postedAt: Date = new Date(Calendar.getInstance().getTime().getTime())) // timestamp
  
object Item {

  lazy val database = Database.forDataSource(DB.getDataSource())

  val ItemTable = new Table[Item]("item") {
    def id = column[Long]("item_id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def postedBy = column[Long]("posted_by_uid")
    def postedTo = column[Long]("posted_to")
    def link = column[String]("link")
    def content = column[String]("content")
    def postedAt = column[Date]("posted_timestamp")
    def * = id.? ~ title ~ postedBy ~ postedTo ~ link.? ~ content ~ postedAt <> (Item.apply _, Item.unapply _)
    def forInsert = title ~ postedBy ~ postedTo ~ link.? ~ content ~ postedAt <> 
      ({ t => Item(None, t._1, t._2, t._3, t._4, t._5, t._6)}, 
      { (i: Item) => Some((i.title, i.postedBy, i.postedTo, i.link, i.content, i.postedAt))})
  }
  
  def findAllBySub(subId: Long): Seq[Item] = database.withSession { implicit db: Session =>
    Query(ItemTable).filter(i => i.postedTo === subId).list
  }
  
  def totalNumberItems: Long = database.withSession { implicit db: Session =>
    Query(ItemTable.length).first
  }

  def create(item: Item): Option[Long] = database.withSession { implicit db: Session =>
    Logger.info("creating new item")
    ItemTable.forInsert returning ItemTable.id.? insert item
  }

  def findById(itemId: Long): Option[Item] = database.withSession { implicit db: Session =>
    Query(ItemTable).filter(i => i.id === itemId).firstOption
  }


}
