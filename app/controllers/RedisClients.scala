package controllers

import play.api._
import play.api.Logger

import play.api.Play.current

import com.redis._

object RedisClients {

  val maybeRedisURL = scala.util.Properties.envOrNone("REDISTOGO_URL")

  val (redisHost, redisPort) = maybeRedisURL match {
    case Some(redisURL) => {
      val redisURI = new java.net.URI(redisURL)
      (redisURI.getHost(), redisURI.getPort())
    }
    case None => {
      (Play.configuration.getString("redis.host").getOrElse("localhost"),
       Play.configuration.getInt("redis.port").getOrElse(6379))
    }
  }

  val clientPool = new RedisClientPool(redisHost, redisPort)

}
