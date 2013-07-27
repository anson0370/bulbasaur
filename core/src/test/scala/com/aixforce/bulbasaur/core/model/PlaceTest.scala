package com.aixforce.bulbasaur.core.model

import com.aixforce.bulbasaur.core.Place
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class PlaceTest extends FunSuite with ShouldMatchers {
  test("place") {
    val process1 = new Process("testProcess1", "test", 1, true)
    val process2 = new Process("testProcess2", "test", 1, false)
    val process1_2 = new Process("testProcess2", "test", 2, false)
    val process1_3 = new Process("testProcess2", "test", 3, true)

    Place += process1 += process2 += process1_2

    // find default
    Place("testProcess1").name should be("testProcess1")
    try {
      Place("testProcess2")
      // never run to here
      true should be(false)
    } catch {
      case e: NullPointerException => // do nothing
    }
    // find with version
    Place("testProcess2", 1).name should be("testProcess2")
    Place("testProcess2", 2).version should be(2)

    // cover default version
    Place += process1_3

    // find new default
    Place("testProcess2").version should be(3)
  }
}