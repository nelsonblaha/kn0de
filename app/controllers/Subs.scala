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

object Subs extends AuthController {
  
  def index(sub: String) = MaybeAuthenticated { implicit maybeUser => implicit request =>

    val frontpageItems = Sub.frontpage
    
    Ok("hi")
  }

}
