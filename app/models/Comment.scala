package models

import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.json.Json

import play.api.Logger

import anorm._
import anorm.SqlParser._

import java.util.Date

import auth._

case class Comment(id: Pk[Long] = NotAssigned,
                   parentId: Pk[Long],
                   itemId: Pk[Long],
                   content: String,
                   postedBy: Long, // user id
                   postedAt: Date,
                   score: Double)

object Comment {

  val simple = {
    get[Pk[Long]]("comment.comment_id") ~
    get[Pk[Long]]("comment.parent_id") ~
    get[Pk[Long]]("comment.item_id") ~
    get[String]("comment.content") ~
    get[Long]("comment.posted_by") ~
    get[Date]("comment.posted_at") ~
    get[Double]("comment.score") map {
      case id~parentId~itemId~content~postedBy~postedAt~score =>
      Comment(id, parentId, itemId, content, postedBy, postedAt, score)
    }
  }

  def findById(id: Long): Option[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comment where comment_id = {id}").on(
        'id -> id
      ).as(Comment.simple.singleOpt)
    }
  }

  def findByParent(parentId: Long): Seq[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comment where parent_id = {parent_id}").on(
        'parent_id -> parentId
      ).as(Comment.simple *)
    }
  }

  def findByItem(itemId: Long): Seq[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comment where item_id = {item_id}").on(
        'item_id -> itemId
      ).as(Comment.simple *)
    }
  }

  def findByAccount(accountId: Long): Seq[Comment] = {
    DB.withConnection { implicit connection =>
      SQL("select * from comment where posted_by = {account_id}").on(
        'account_id -> accountId
      ).as(Comment.simple *)
    }
  }


}

