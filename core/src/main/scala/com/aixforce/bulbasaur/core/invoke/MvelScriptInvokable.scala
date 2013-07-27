package com.aixforce.bulbasaur.core.invoke

import scala.collection.Map
import scala.xml.Node

import com.aixforce.bulbasaur.helper.Helper.attrStrOrElse
import com.aixforce.bulbasaur.helper.MvelHelper
import com.aixforce.bulbasaur.core.CoreModule
import org.mvel2.PropertyAccessException
import java.lang.reflect.InvocationTargetException

class MvelScriptInvokable(node: Node) extends Invokable {

  private val expr = node.attrStrOrElse("expr")("")

  private val script = node.attrStrOrElse("script") {
    require(node.text != "", "script text can't be null in script tag")
    node.text.trim()
  }

  override protected val async = node.attrStrOrElse("async")("false").toBoolean

  private val beans = node.attrStrOrElse("beans")("") match {
    case "" => Map.empty[String, Any]
    case beansStr => CoreModule.springContext match {
      case null => throw new NullPointerException("can't get spring bean because can't find spring context")
      case springContext => beansStr.split(",").foldLeft(Map.empty[String, Any]) {
        (beans, bean) =>
          beans + (bean.trim -> springContext.getBean(bean.trim))
      }
    }
  }

  private val pojos = node.attrStrOrElse("pojos")("") match {
    case "" => Map.empty[String, Any]
    case pojosStr => pojosStr.split(",").foldLeft(Map.empty[String, Any]) {
      (pojos, pojo) =>
        val pojoDefine = pojo.trim.split("->")
        pojos + (pojoDefine(1).trim -> Class.forName(pojoDefine(0).trim).newInstance())
    }
  }

  override def invoke0(context: Map[String, Any]): Any = {
    val evalContext = context ++ beans ++ pojos
    if (expr == "" || MvelHelper.evalToBoolean(expr, evalContext)) {
      try {
        MvelHelper.eval(script, evalContext)
      } catch {
        // unboxing mvel's exception, throw origin exception
        case pae: PropertyAccessException => {
          throw MvelHelper.unboxingException(pae)
        }
      }
    } else {
      null
    }
  }

  override def toString = "script:" + script
}