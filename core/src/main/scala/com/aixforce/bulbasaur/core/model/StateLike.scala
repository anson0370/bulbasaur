package com.aixforce.bulbasaur.core.model

import scala.collection.Map
import scala.xml.Node

trait StateLike {
  def name: String

  def prepare(context: Map[String, Any]): (Boolean, Map[String, Any])

  def execute(context: Map[String, Any]): (Boolean, Map[String, Any])

  def willGo(context: Map[String, Any]): Option[String]

  def parse(xml: Node): StateLike
}