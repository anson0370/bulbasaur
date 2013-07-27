package com.aixforce.bulbasaur.core.model

import scala.collection.Map

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 11-12-22
 */

trait StateTrait {
  def beforePrepare(context: Map[String, Any], state: StateLike): Map[String, Any] = {null}
  def afterPrepare(context: Map[String, Any], state: StateLike) {}

  def beforeExecute(context: Map[String, Any], state: StateLike): Map[String, Any] = {null}
  def afterExecute(context: Map[String, Any], state: StateLike) {}
}