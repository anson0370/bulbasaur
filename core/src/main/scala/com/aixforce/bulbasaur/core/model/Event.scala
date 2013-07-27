package com.aixforce.bulbasaur.core.model

import scala.collection.Map
import scala.xml.Node
import com.aixforce.bulbasaur.core.invoke.Invokable
import com.aixforce.bulbasaur.core.invoke.MvelScriptInvokable
import com.aixforce.bulbasaur.helper.Helper.attrStrOrElse

@StateMeta(t = "event")
class Event extends State {
  private[this] var preInvokes = List.empty[(String, Invokable)]

  override def prepare(context: Map[String, Any]): (Boolean, Map[String, Any]) = {
    (false, preInvokes.foldLeft(Map.empty[String, Any]) {
      (resultMap, invoke) =>
        invoke match {
          case (returnKey, invokable) => {
            val result = invokable.invoke(context ++ resultMap)
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
    (xml \ "pre-invokes").foreach {
      invokesNode =>
        invokesNode.child.foreach {
          node =>
            node.label match {
              case "#PCDATA" => // do nothing for #PCDATA
              case "script" => {
                val returnKey = node.attrStrOrElse("return")(null)
                preInvokes = (returnKey, new MvelScriptInvokable(node)) :: preInvokes
              }
              case unknown => throw new RuntimeException("unknown label in invokes tag: " + unknown)
            }

        }
    }
    preInvokes = preInvokes.reverse
    this
  }
}