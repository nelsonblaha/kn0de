package models

import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.json.Json

import play.api.Logger

import scala.slick.driver.PostgresDriver.simple._

import java.sql.Date 

import java.util.Calendar

import auth._

case class Comment(id: Option[Long] = None,
                   parentId: Option[Long] = None,
                   itemId: Long,
                   content: String,
                   postedBy: Long, // user id
                   postedAt: Date = new Date(Calendar.getInstance().getTime().getTime()))

object Comment {

  def database = Database.forDataSource(DB.getDataSource())

  val CommentTable = new Table[Comment]("comment") {
    def id = column[Long]("comment_id", O.PrimaryKey, O.AutoInc)
    def parentId = column[Long]("parent_id", O.PrimaryKey)
    def itemId = column[Long]("item_id", O.PrimaryKey)
    def content = column[String]("content")
    def postedBy = column[Long]("posted_by")
    def postedAt = column[Date]("posted_at")
    def * = id.? ~ parentId.? ~ itemId ~ content ~ postedBy ~ postedAt <> (Comment.apply _, Comment.unapply _)
    def topLevel = itemId ~ content ~ postedBy ~ postedAt <>
      ({ c => Comment(None, None, c._1, c._2, c._3, c._4)}, { (c: Comment) => Some((c.itemId, c.content, c.postedBy, c.postedAt)) })
    def withParent = parentId ~ itemId ~ content ~ postedBy ~ postedAt <>
      ({ c => Comment(None, Some(c._1), c._2, c._3, c._4, c._5)}, { (c: Comment) => Some((c.parentId.get, c.itemId, c.content, c.postedBy, c.postedAt)) })
  }

  def findById(id: Long): Option[Comment] = database.withSession { implicit db: Session =>
    Query(CommentTable).filter(c => c.id === id).firstOption
  }

  def findByParent(parentId: Long): Seq[Comment] = database.withSession { implicit db: Session =>
    Query(CommentTable).filter(c => c.parentId === parentId).list
  }

  def findByItem(itemId: Long): Seq[Comment] = database.withSession { implicit db: Session =>
    Query(CommentTable).filter(c => c.itemId === itemId).list
  }

  def findByAccount(accountId: Long): Seq[Comment] = database.withSession { implicit db: Session =>
    Query(CommentTable).filter(c => c.postedBy === accountId).list
  }


}

