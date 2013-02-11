package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.RequestHeader
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import models._
import models.auth._
import views._

object Subs extends AuthController with Header {

  val itemForm = Form(
    mapping(
      "title" -> text,
      "postedBy" -> longNumber,
      "postedTo" -> longNumber,
      "link" -> optional(text),
      "content" -> text
    )((title, postedBy, postedTo, link, content) => Item(None, title, postedBy, postedTo, 0, link, content))
     ((item: Item) => Some((item.title, item.postedBy, item.postedTo, item.link, item.content)))
  )
      
  
  def index(subName: String) = MaybeAuthenticated { implicit maybeUser => implicit request =>
    (for {
      sub <- (Sub.findByName(subName)    toRight "Could not find sub with that name").right
      id <- (sub.id                      toRight "No ID set for that sub").right
      frontpage <- (Sub.frontpage(id)    toRight "No frontpage items found for sub").right
    } yield Ok(views.html.sub(sub, frontpage))) fold (
      error => BadRequest(error),
      ok => ok
    )
  }

  def createItem = MaybeAuthenticated { implicit maybeUser => implicit request =>
    (for {
      item <- (itemForm.bindFromRequest.value  toRight "Could not bind form to value").right
      subId <- (Item.create(item)              toRight "Could not get unique ID for new item").right
      sub <- (Sub.findById(subId)              toRight "Could not find sub by ID: " + subId).right
      items <- (Sub.frontpage(subId)           toRight "Could not get frontpage items for sub by ID: " + subId).right
    } yield Ok(views.html.sub(sub, items))) fold (
      error => BadRequest(error),
      ok => ok
    )
  }


}
