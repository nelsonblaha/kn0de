import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "kn0de"
  val appVersion      = "1.0-SNAPSHOT"
    
//  lazy val authModule = Project("play20-auth-module", file("modules/auth"), settings = Defaults.defaultSettings ++ buildSettings)
//   Seq(libraryDependencies ++= Seq("play" %% "play" % "2.1.0"), moduleConfigurations)

  val appDependencies = Seq(
    jdbc,
    anorm,
    "com.typesafe" %% "slick" % "1.0.0-RC2",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "net.debasishg" % "redisclient_2.10" % "2.9",
    "org.webjars" % "bootstrap" % "2.2.2",
    "jp.t2v" %% "play20.auth" % "0.5",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.scala-lang" % "scala-compiler" % "2.10.0"
  )

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.0",
    resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
    resolvers += "jbcrypt repo" at "http://mvnrepository.com/"
  ).dependsOn(play21auth)

  lazy val play21auth = uri("https://github.com/ryantanner/play20-auth.git#play21")

}
