package models.auth

import play.api.db._
import play.api.libs.json._
import play.api.Logger
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import java.sql.Clob
import org.mindrot.jbcrypt.BCrypt

case class Account(id: Pk[Long], email: String, password: String, name: String, permission: Permission)

object Account {

  object Clob {
    def unapply(clob: Clob): Option[String] = Some(clob.getSubString(1, clob.length.toInt))
  }

  implicit val rowToPermission: Column[Permission] = {
    Column.nonNull[Permission] { (value, meta) =>
      value match {
        case Clob("Administrator") => Right(Administrator)
        case Clob("NormalUser") => Right(NormalUser)
        case _ => Left(TypeDoesNotMatch(
          "Cannot convert %s : %s to Permission for column %s".format(value, value.getClass, meta.column)))
      }
    }
  }

  implicit def permissionToString(permission: Permission): String = {
    permission match {
      case Administrator => "administrator"
      case NormalUser => "normal_user"
    }
  }
  
  implicit def stringToPermission(permission: String) = {
    permission match {
      case "administrator" => Administrator
      case "normal_user" => NormalUser
    }
  }

  val simple = {
    get[Pk[Long]]("account.id") ~
    get[String]("account.email") ~
    get[String]("account.password") ~
    get[String]("account.name") ~
    get[String]("account.permission") map {
      case id~email~pass~name~perm => Account(id, email, pass, name, perm)
    }
  }

  def authenticate(email: String, password: String): Option[Account] = {
    Logger.info("[Account] Authenticating %s/%s".format(email, password))
    Logger.info("[Account] Salt should equal %s".format(BCrypt.hashpw(password, BCrypt.gensalt())))

    DB.withConnection { implicit connection =>
      val salt = SQL("SELECT password FROM account WHERE id = 1").as(scalar[String].single)
      Logger.info("[Account] Salt in db equals %s".format(salt))
    }

    findByEmail(email).filter { account => BCrypt.checkpw(password, account.password) }
  }

  def findByEmail(email: String): Option[Account] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM account WHERE email = {email}").on(
        'email -> email
      ).as(simple.singleOpt)
    }
  }

  def findById(id: Long): Option[Account] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM account WHERE id = {id}").on(
        'id -> id
      ).as(simple.singleOpt)
    }
  }

  def findAll: Seq[Account] = {
    DB.withConnection { implicit connection =>
      SQL("select * from account").as(simple *)
    }
  }

  def isAdmin(accountId: Long): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select count(account.id) = 1 from account
          where account.id = {id} and account.permission = 'administrator'
        """
      ).on(
        'id -> accountId
      ).as(scalar[Boolean].single)
    }
  }

  def create(account: Account): Option[Long] = {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO account VALUES (DEFAULT, {email}, {pass}, {name}, {permission})").on(
        'email -> account.email,
        'pass -> BCrypt.hashpw(account.password, BCrypt.gensalt()),
        'name -> account.name,
        'permission -> account.permission.toString
      ).executeInsert()
    }
  }

  def update(account: Account): Int = {
    Logger.info("[Account] updating account id = %d".format(account.id.getOrElse(0)))
    DB.withConnection { implicit connection =>
      SQL("""
        update account
        set email = {email},
        password = {password},
        name = {name}
        where id = {id}
        """
      ).on(
        'email -> account.email,
        'password -> BCrypt.hashpw(account.password, BCrypt.gensalt()),
        'name -> account.name,
        'id -> account.id
      ).executeUpdate()
    }
  }

  implicit object AccountFormat extends Format[Account] {

    def writes(a: Account): JsValue = JsObject(Seq(
      "id" -> JsNumber(a.id.get),
      "name" -> JsString(a.name),
      "email" -> JsString(a.email),
      "permission" -> JsString(a.permission)
    ))

    def reads(json: JsValue): Account = Account(
      (json \ "id").asOpt[Long].map(id =>
        Id(id)
      ).getOrElse(NotAssigned),
      (json \ "email").as[String],
      (json \ "password").as[String],
      (json \ "name").as[String],
      (json \ "permission").as[String]
    )

  }
}
