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
    tuple(
      "title" -> text,
      "link" -> optional(text),
      "content" -> text
    )
  )
  
  def index(subName: String) = MaybeAuthenticated { implicit maybeUser => implicit request =>
    (for {
      sub <- (Sub.findByName(subName)    toRight "Could not find sub with that name").right
      id <- (sub.id                      toRight "No ID set for that sub").right
      frontpage <- (Sub.frontpage(id)    toRight "No frontpage items found for sub").right
    } yield Ok(views.html.sub.index(sub, frontpage))) fold (
      error => BadRequest(error),
      ok => ok
    )
  }

  def newItem(subName: String) = IsAuthenticated { implicit user => implicit request =>
    (for {
      sub <- (Sub.findByName(subName)     toRight "Could not find sub with that name").right
    } yield Ok(views.html.newItem(sub, itemForm))) fold (
      error => BadRequest(error),
      ok => ok
    )
  }

  def createItem(subName: String) = IsAuthenticated { implicit user => implicit request =>
    (for {
      (title, link, content) <- (itemForm.bindFromRequest.value  toRight "Could not bind form to value").right
      sub <- (Sub.findByName(subName)              toRight "Could not find sub by name: " + subName).right
      subId <- (sub.id                             toRight "No sub ID").right
      userId <- (user.id                           toRight "No User ID").right
    } yield (sub, Item(None, title, userId, subId, link, content))) fold (
      error => BadRequest(error),
      subAndItem => subAndItem match { case (sub, item) => (for {
          itemId <- (Item.create(item)                 toRight "Could not get unique ID for new item").right
          postedBy <- (Account.findById(item.postedBy) toRight "Could not find user").right
      } yield Ok(views.html.item(sub, item, postedBy))) fold (
          error => BadRequest(error),
          ok => ok
        )
      }
    )
  }

  def singleItem(subName: String, itemId: Long) = MaybeAuthenticated { implicit maybeUser => implicit request =>
    (for {
      sub <-      (Sub.findByName(subName)            toRight "Could not find sub").right
      item <-     (Item.findById(itemId)              toRight "Could not find item").right
      postedBy <- (Account.findById(item.postedBy)    toRight "Could not find poster").right
    } yield Ok(views.html.item(sub, item, postedBy))) fold (
        error => BadRequest(error),
        ok => ok
      )
  }


}
