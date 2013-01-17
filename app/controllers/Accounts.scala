package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import models._
import models.auth._

object Accounts extends AuthController {

  /*
  def listAll = IsAdmin { account => _ =>
    val accounts = Account.findAll

    Ok(Json.toJson(accounts))
  }

  def byId(id: Long) = Action {
    Account.findById(id).map { account =>
      Ok(Json.toJson(account))
    }.getOrElse {
      NotFound(Json.toJson(Map(
        "status" -> "404",
        "message" -> "No such account exists"
      )))
    }
  }

  def create = Action(parse.json) { request =>
    val accountJson = request.body
    val account = accountJson.as[Account]

    Account.create(account)

    Ok
  }
  */

  def updateAll = TODO

  def update(id: Long) = TODO

  def deleteAll = TODO

  def delete(id: Long) = TODO

}
