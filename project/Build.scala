import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "kn0de"
  val appVersion      = "1.0-SNAPSHOT"
    
  lazy val authModule = Project("play20-auth-module", file("modules/auth"))

  val appDependencies = Seq(
    jdbc,
    anorm,
    "com.typesafe" %% "slick" % "1.0.0-RC2",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "org.webjars" % "bootstrap" % "2.2.2",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.scala-lang" % "scala-compiler" % "2.10.0"
  )

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.0",
    resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
    resolvers += "jbcrypt repo" at "http://mvnrepository.com/"
  ).dependsOn(authModule)

}
