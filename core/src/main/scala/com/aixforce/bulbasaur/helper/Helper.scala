package com.aixforce.bulbasaur.helper

import scala.collection.immutable.{Map => ImmutableMap}
import scala.collection.mutable.{Map => MutableMap}
import xml.{XML, Elem, Node, Text}
import collection.{GenTraversableOnce, JavaConversions, Map}

class BooleanEx(b: Boolean) {
  def /?[T](func: Boolean => T) = {
    func(b)
  }
}

class AnyEx[T](o: => T) {
  def ?/(o1: => T) = {
    b: Boolean => {
      if (b) {
        o
      } else {
        o1
      }
    }
  }

  def when(b: Boolean) {
    if (b) o
  }

  def unless(b: Boolean) {
    if (!b) o
  }
}

/**
 * 1. ?:
 *  example: (1 == 1) ? (true or false)
 * 2. scalaMap[String, Any] <-> javaMap[String, Object]
 */
object Helper {
  def using[T <: {def close()}, Y](resource: T)(func: T => Y) = {
    try {
      func(resource)
    } finally {
      if (resource != null) resource.close()
    }
  }

  implicit def StringPlus(str: String) = new {
    def toXML: Elem = {
      XML.loadString(str)
    }
  }

  implicit def GenTraversableOncePlus[A](gen: GenTraversableOnce[A]) = new {
    def foreachWithIndex[B](func: (A, Int) => B) {
      (0 /: gen) {
        (index, elem) =>
          func(elem, index)
          index + 1
      }
    }
  }

  implicit def Boolean2BooleanEx(b: Boolean) = new BooleanEx(b)

  implicit def Any2AnyEx[T](o: => T) = new AnyEx(o)

  implicit def copy2JavaMap(scalaMap: Map[String, Any]): java.util.Map[String, Object] = {
    val jMap = new java.util.HashMap[String, Object]
    scalaMap.foreach {
      entry =>
        jMap.put(entry._1, entry._2.asInstanceOf[Object])
    }
    jMap
  }

  implicit def scalaMap_T_S_A_2JavaMap_T_S_O(scalaMap: Map[String, Any]) = {
    if (scalaMap.isInstanceOf[MutableMap[String, Any]]) {
      JavaConversions.mutableMapAsJavaMap(scalaMap.asInstanceOf[MutableMap[String, Object]])
    } else {
      JavaConversions.mapAsJavaMap(scalaMap.asInstanceOf[Map[String, Object]])
    }
  }

  implicit def javaMap_T_S_O_2scalaMap_T_S_A(javaMap: java.util.Map[String, Object]) = {
    JavaConversions.mapAsScalaMap(javaMap.asInstanceOf[java.util.Map[String, Any]])
  }

  implicit def attrStrOrElse(node: Node) = new {
    def attrStrOrElse(attr: String)(default: => String): String = {
      node.attribute(attr) match {
        case None | Some(Nil) => default
        case Some(Text(attrStr)) => attrStr
      }
    }
  }

  implicit def nullOrElse[T](anyRef: T) = new {
    def ?(elseRef: => T): T = {
      if (anyRef.asInstanceOf[AnyRef] != null) anyRef else elseRef
    }
  }

  implicit def makeString[T](list: GenTraversableOnce[T]) = new {
    def makeString(split: String, func: T => String): String = {
      if (list.size == 0) {
        ""
      } else {
        val result = list.foldLeft("") {
          (result, element) =>
            result + func(element) + split
        }
        result.substring(0, result.length - split.length)
      }
    }
  }

  def main(args: Array[String]) {
    val a = (1 == 2) /? {println(1);1} ?/ {println(2);2}
    println(a)
    println(1) when 1 == 2

    println(1) unless 1 == 2

    val nullRef: AnyRef = null
    println(nullRef ? "right")
    val notNullRef = "wtf"
    println(notNullRef ? "wrong")
  }
}