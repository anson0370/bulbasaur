package com.aixforce.bulbasaur.helper

import scala.collection.Map
import org.mvel2.{PropertyAccessException, MVEL}
import java.lang.reflect.InvocationTargetException

object MvelHelper {

  def eval(expr: String, vars: Map[String, Any]): AnyRef = {
    // can be faster: use wrapper but not copy, copy is more security
    val jMapVars = Helper.copy2JavaMap(vars)
    MVEL.eval(expr, jMapVars)
  }

  def evalToBoolean(expr: String, vars: Map[String, Any]): Boolean = {
    // can be faster: use wrapper but not copy, copy is more security
    val jMapVars = Helper.copy2JavaMap(vars)
    MVEL.evalToBoolean(expr, jMapVars).asInstanceOf[Boolean]
  }

  def unboxingException(ex: Throwable): Throwable = {
    ex match {
      case pae: PropertyAccessException =>
        val innerEx = getCause(pae)
        if (innerEx.isInstanceOf[InvocationTargetException]) {
          innerEx.asInstanceOf[InvocationTargetException].getCause
        } else {
          innerEx
        }
      case _ => ex
    }
  }

  private def getCause(pae: PropertyAccessException): Throwable = {
    if (pae.getCause == null) {
      pae
    } else if (pae.getCause.isInstanceOf[PropertyAccessException] && !(pae.getCause eq pae)) {
      getCause(pae.getCause.asInstanceOf[PropertyAccessException])
    } else {
      pae.getCause
    }
  }
}