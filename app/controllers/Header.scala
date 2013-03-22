package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.RequestHeader
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import scala.reflect.{ClassTag, classTag}

import models._
import models.auth._

case class UserInfo(
  maybeUser: Option[Account],
  subscriptions: List[Subscription],
  loginForm: Form[Option[Account]]
)

trait Header {

  implicit def withUserInfoOptionalUser(implicit maybeUser: Option[Account]): UserInfo = {
    UserInfo(
      maybeUser,
      Subscription.findByAccount(maybeUser),
      loginForm
    )
  }

  implicit def withUserInfo(implicit user: Account): UserInfo = {
    UserInfo(
      Some(user),
      Subscription.findByAccount(Some(user)),
      loginForm
    )
  }

  val loginForm = Form {
    mapping("name" -> text, "password" -> text)(Account.authenticate)(_.map(u => (u.name, "")))
      .verifying("Invalid name or password", result => result.isDefined)
  }

}
