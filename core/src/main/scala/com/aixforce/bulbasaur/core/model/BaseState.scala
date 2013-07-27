package com.aixforce.bulbasaur.core.model

import scala.collection.Map
import scala.xml.Node
import com.aixforce.bulbasaur.helper.Helper.attrStrOrElse

abstract class BaseState extends StateLike {
  private var stateName: String = _
  private var paths = List.empty[PathLike]

  override def name = stateName

  override def prepare(context: Map[String, Any]): (Boolean,  Map[String, Any]) = {
    // do nothing but return empty map
    // need override
    (true, Map.empty[String, Any])
  }

  override def execute(context: Map[String, Any]): (Boolean, Map[String, Any]) = {
    // do nothing but return empty map
    // need override
    (true, Map.empty[String, Any])
  }

  override def willGo(context: Map[String, Any]): Option[String] = {
    paths.find(_.can(context)) match {
      case None => None
      case Some(path) => Some(path.to)
    }
  }

  override def parse(xml: Node): StateLike = {
    stateName = xml.attrStrOrElse("name") {
      throw new IllegalArgumentException("attribute name in state node is required")
    }
    // parse path
    (xml \ "paths" \ "path").foreach {
      node =>
        val pathTo = node.attrStrOrElse("to") {
          throw new IllegalArgumentException("attribute to in path node is required")
        }
        val expr = node.attrStrOrElse("expr")(null)
        paths = new Path(pathTo, expr) :: paths
    }
    paths = paths.reverse
    this
  }

  override def toString = super.toString + " | " + name
}