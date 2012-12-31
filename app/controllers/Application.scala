package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.RequestHeader
import play.api.data._
import play.api.data.Forms._

import models._
import models.auth._
import views._

import jp.t2v.lab.play20.auth._

object Application extends AuthController with LoginLogout {

  def index = MaybeAuthenticated { implicit maybeUser => implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  val loginForm = Form {
    mapping("name" -> text, "password" -> text)(Account.authenticate)(_.map(u => (u.name, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Logout page
   */
  def logout = Action { implicit request =>
    gotoLogoutSucceeded.flashing(
      "success" -> "You've been logged out"
    )
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => gotoLoginSucceeded(user.get.id.get)
    )
  }
  
}

trait AuthConfigImpl extends AuthConfig {

  /** 
   * A type that is used to identify a user.
   * `String`, `Int`, `Long` and so on. 
   */
  type Id = Long

  /** 
   * A type that represents a user in your application.
   * `User`, `Account` and so on.
   */
  type User = Account

  /** 
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Permission
   * case object Administrator extends Permission
   * case object NormalUser extends Permission
   */
  type Authority = User => Boolean

  /**
   * A `ClassManifest` is used to retrieve an id from the Cache API.
   * Use something like this:
   */
  val idManifest: ClassManifest[Id] = classManifest[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 36000

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id): Option[User] = Account.findById(id)

  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded(request: RequestHeader): PlainResult = Redirect(routes.Application.index)

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader): PlainResult = Redirect(routes.Application.login)

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed(request: RequestHeader): PlainResult = Redirect(routes.Application.login)

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  def authorizationFailed(request: RequestHeader): PlainResult = Forbidden("no permission")

  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authority: Authority): Boolean = 
    (user.permission, authority) match {
      case (Administrator, _) => true
      case (NormalUser, _) if(authority(user)) => true
      case _ => false
    }
  

}

trait AuthController extends Controller with Auth with AuthConfigImpl {

  protected def IsModOf(subId: Long) = authorizedAction(isModOf(subId) _) _

  private def isModOf(subId: Long)(account: Account): Boolean = 
    Sub.isMod(subId, account.id.get)
  
  protected def IsAdmin = authorizedAction(isAdmin) _

  private def isAdmin(account: Account): Boolean = 
    account.permission == Administrator

  protected def IsAuthenticated = authorizedAction((a: Account) => true) _

  protected def MaybeAuthenticated = optionalUserAction(BodyParsers.parse.anyContent) _

}
