package com.aixforce.bulbasaur.helper

import test.{TestBeanJava, TestBean}
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-2-7
 */

class MvelHelperTest extends FunSuite with ShouldMatchers with Logger {
  test("eval to boolean") {
    val expr = "a == 1 && b == 2"
    val result = MvelHelper.evalToBoolean(expr, Map("a" -> 1, "b" -> 3))
    logger.info("result of " + expr + " is " + result)
    result should be(false)
  }

  test("eval") {
    val expr = "b == 2; a = 1"
    MvelHelper.eval(expr, Map("b" -> 3)) should be(1)
  }

  test("null parameter") {
    val expr = "testBean.testNullParameter(p)"
    MvelHelper.eval(expr, Map("testBean" -> new TestBean, "p" -> null))
  }

  test("change parameter method") {
    val expr = "testBean.testChangeParamMethod(new Object[] {o1, o2, o3})"
    MvelHelper.eval(expr, Map("testBean" -> new TestBeanJava, "o1" -> 1, "o2" -> "2", "o3" -> null))
  }
}