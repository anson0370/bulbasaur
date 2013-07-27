package com.aixforce.bulbasaur.core.model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class PathTest extends FunSuite with ShouldMatchers {
  test("path can go the right way") {
    // true
    var path = new Path("to", "true")
    path.can(null) should be === true
    // Int judge
    path = new Path("to", "param == 1")
    path.can(Map("param" -> 1)) should be === true
    // String judge
    path = new Path("to", "if (param == \"123\") {true} else {false}")
    path.can(Map("param" -> "123")) should be === true
    // blank expr
    path = new Path("to", null)
    path.can(null) should be === true
    // null judege
    path = new Path("to", "null")
    path.can(null) should be === false
  }
}