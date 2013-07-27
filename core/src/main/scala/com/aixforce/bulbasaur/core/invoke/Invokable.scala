package com.aixforce.bulbasaur.core.invoke

import scala.collection.Map
import com.aixforce.bulbasaur.helper.Logger
import akka.actor.{Actor, ActorDSL, ActorSystem}

abstract class Invokable extends Logger {
  protected val async = false

  implicit val system = ActorSystem("invokable")

  final def invoke(context: Map[String, Any]): Any = {
    if (async) {
      ActorDSL.actor(new Actor {
        def receive = {
          case msg: Map[String, Any] =>
            logger.info("async invoke request received, params:" + msg + "\ninvokable:" + this)
            invoke0(msg)
          context.stop(self)
        }
      }) ! context
      null
    } else {
      invoke0(context)
    }
  }

  def invoke0(context: Map[String, Any]): Any
}