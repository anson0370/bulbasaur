package com.aixforce.bulbasaur.core

import com.aixforce.bulbasaur.helper.Logger
import com.aixforce.bulbasaur.framework.{Context, Module}

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-9-8
 */

object Bulbasaur extends Logger {
  private val defaultModules: List[Module] = List(CoreModule)

  var initialized = false

  var context: Context = _

  /**
   * try to get require module & init<br/>
   * it will calc all dependency module and init it in order
   */
  def require() {
    require(defaultModules)
  }

  def require(module1: Module, modules: Module*) {
    require(defaultModules ::: module1 :: modules.toList)
  }

  def require(modules: Array[Module]) {
    if (modules == null && modules.isEmpty) {
      require(defaultModules)
    } else {
      require(modules.toList)
    }
  }

  private def require(modules: List[Module]) {
    if (initialized == true) {
      logger.warn("!Pikachu already initialized!")
    } else {
      context = new Context
      context.init(modules)
      initialized = true
    }
  }
}