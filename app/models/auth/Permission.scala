package models.auth

sealed trait Permission
case object Administrator extends Permission
case object NormalUser extends Permission

trait Administrator {

  def apply = true

}
