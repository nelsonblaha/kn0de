package models.auth

/*
import scala.slick.lifted.{MappedTypeMapper,BaseTypeMapper,TypeMapperDelegate}
import scala.slick.driver.BasicProfile
import scala.slick.session.{PositionedParameters,PositionedResult}
import language.implicitConversions

object PermissionType {

  sealed trait Permission
  case object Administrator extends Permission
  case object NormalUser extends Permission

  implicit def permissionToString(permission: Permission): String = {
    permission match {
      case Administrator => "Administrator"
      case NormalUser => "NormalUser"
    }
  }
  
  implicit def stringToPermission(permission: String): Permission = {
    permission match {
      case "Administrator" => Administrator
      case "NormalUser" => NormalUser
    }
  }

  implicit val permissionTypeMapper = MappedTypeMapper.base[Permission, String](
    p => p,
    s => s
  )

}
*/
