package com.aixforce.bulbasaur.core.model

import java.lang.String

class Process(val name: String, var first: String, val version: Int, var isDefault: Boolean) {
  private[this] var states = Map.empty[String, StateLike]

  def getState(name: String) = {
    states.get(name)
  }

  def addState(state: StateLike): this.type = {
    states += (state.name -> state)
    this
  }

  override def toString = {
    "process - " + name + "$" + version
  }
}