package com.aixforce.bulbasaur.core.model

import scala.collection.Map
import scala.xml.Node

import com.aixforce.bulbasaur.core.invoke.Invokable
import com.aixforce.bulbasaur.core.invoke.MvelScriptInvokable
import com.aixforce.bulbasaur.helper.Helper.attrStrOrElse
import com.aixforce.bulbasaur.helper.Logger

@StateMeta(t = "state")
class State extends BaseState with Logger {
  private[this] var invokes = List.empty[(String, Invokable)]

  override def execute(context: Map[String, Any]): (Boolean, Map[String, Any]) = {
    (true, invokes.foldLeft(Map.empty[String, Any]) {
      (resultMap, invoke) =>
        invoke match {
          case (returnKey, invokable) => {
            val result = invokable.invoke(context ++ resultMap)
            if (logger.isDebugEnabled) {
              logger.debug("state invoke result:[" + returnKey + " -> " + result + "]")
            }
            if (result != null && returnKey != null) {
              resultMap + (returnKey -> result)
            } else {
              resultMap
            }
          }
        }
    })
  }

  override def parse(xml: Node): StateLike = {
    super.parse(xml)
    (xml \ "invokes").foreach {
      invokesNode =>
        invokesNode.child.foreach {
          node =>
            node.label match {
              case "#PCDATA" => // do nothing for #PCDATA
              case "script" => {
                val returnKey = node.attrStrOrElse("return")(null)
                invokes = (returnKey, new MvelScriptInvokable(node)) :: invokes
              }
              case unknown@_ => throw new RuntimeException("unknown label in invokes tag: " + unknown)
            }

        }
    }
    invokes = invokes.reverse
    this
  }
}