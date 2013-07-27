package com.aixforce.bulbasaur.framework

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-12-5
 */

class FrameworkUtilTest extends FunSuite with ShouldMatchers {
  test("order module") {
    val result = FrameworkUtil.orderModules(List(ModuleD))
    print(result)
  }
}

object ModuleX extends Module {
}

object ModuleA extends Module {
  override val require = Array[Module](ModuleX)
}

object ModuleB extends Module

object ModuleC extends Module {
  override val require = Array[Module](ModuleA)
}

object ModuleD extends Module {
  override val require = Array[Module](ModuleC)
  override val optionalRequire = Array[Module](ModuleB)
}