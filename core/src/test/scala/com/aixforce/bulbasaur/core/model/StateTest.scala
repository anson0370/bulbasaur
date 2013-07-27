package com.aixforce.bulbasaur.core.model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class StateTest extends FunSuite with ShouldMatchers {
  test("state will go") {
    val state = new State()
    val xml = <state name="state">
                <paths>
                  <path to="1" expr="i==1"/>
                  <path to="2" expr="i==2"/>
                </paths>
              </state>
    state.parse(xml)
    val result = state.willGo(Map("i" -> 2))
    result.get should be ("2")
  }
}