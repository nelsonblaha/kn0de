package controllers

import play.api._
import play.api.Logger

import play.api.Play.current

import com.redis._

object RedisClients {

  val clientPool = new RedisClientPool(
    Play.configuration.getString("redis.host").getOrElse("localhost"),
    Play.configuration.getInt("redis.port").getOrElse(6379)
  )

}
