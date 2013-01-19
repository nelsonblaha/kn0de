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

case class Item(id: Pk[Long] = NotAssigned,
                title: String,
                postedBy: Long, // user id
                postedTo: Long, // sub id
                score: Double,
                link: String,
                content: String,
                postedAt: Date) // timestamp
  
object Item {

  val simple = {
    get[Pk[Long]]("item.item_id") ~
    get[String]("item.title") ~
    get[Long]("item.posted_by_uid") ~
    get[Long]("item.posted_to") ~
    get[Double]("item.score") ~
    get[String]("item.link") ~
    get[String]("item.content") ~
    get[Date]("item.posted_timestamp") map {
      case id~title~postedBy~postedTo~score~link~content~postedAt =>
      Item(id, title, postedBy, postedTo, score, link, content, postedAt)
    }
  }

  def findAllBySub(subId: Long): Seq[Item] = {
    DB.withConnection { implicit connection =>
      SQL("select * from item where item.sub_id = {sub_id}").on(
        'sub_id -> subId
      ).as(Item.simple *)
    }
  }

  def totalNumberItems: Long = {
    DB.withConnection { implicit connection =>
      SQL("select count(*) from item").as(scalar[Long].single)
    }
  }

  def frontpage: Seq[(Item, Account)] = {
    Nil
  }

  def create(item: Item): Option[Long] = {
    Logger.info("creating new item")
    DB.withConnection { implicit connection =>
      SQL("""
          insert into item values 
           (default, {title}, {posted_by}, {posted_to},
           {score}, {link}, {content}, default) returning item_id;
          """).on(
          'title -> item.title,
          'posted_by -> item.postedBy,
          'posted_to -> item.postedTo,
          'score -> item.score,
          'link -> item.link,
          'content -> item.content
        ).as(scalar[Long].singleOpt)
    }
  }

}
