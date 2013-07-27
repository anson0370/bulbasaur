package com.aixforce.bulbasaur.core.model

import scala.collection.Map
import com.aixforce.bulbasaur.helper.MvelHelper
import org.springframework.util.StringUtils

class Path(initTo: String, val expr: String = null) extends PathLike {

  override def to = initTo

  override def can(vars: Map[String, Any]): Boolean = {
    if (!StringUtils.hasText(expr)) {
      true
    } else {
      MvelHelper.evalToBoolean(expr, vars)
    }
  }
}