package com.aixforce.bulbasaur.core.model

import scala.collection.Map

trait PathLike {
  def to: String

  def can(vars: Map[String, Any]): Boolean
}