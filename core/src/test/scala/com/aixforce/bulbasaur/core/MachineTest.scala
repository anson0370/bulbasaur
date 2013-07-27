package com.aixforce.bulbasaur.core


import com.aixforce.bulbasaur.helper.Logger
import org.apache.log4j.Level
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class MachineTest extends FunSuite with ShouldMatchers with Logger {
  test("machine run") {
    Bulbasaur.require()

    val m = Machine("process")
    m.context_+("goto" -> 2, "i" -> 3)
    m.run()
    println(m.getContext)
    m.context("a") should be === 6
  }

  test("machine run performance") {
    org.apache.log4j.Logger.getRootLogger.setLevel(Level.INFO)
    Bulbasaur.require()

    // parse first
    Machine("process")
    val start = System.currentTimeMillis()
    (0 until 1000).foreach {
      i =>
        val m = Machine("process")
        m.context_+("goto" -> 2, "i" -> 3)
        m.run()
        m.context("a") should be === 6
    }
    val end = System.currentTimeMillis()
    logger.warn("run machine 1000 times, it spend:" + (end - start) + "ms")
  }

  test("machine run with event") {
    Bulbasaur.require()

    val m = Machine("process_with_event")
    m.run()
    println(m.getCurrentStateName)
    m.context("info") should be === "some info"
    val m2 = Machine("process_with_event")
    m2.context_+("goto" -> 2, "i" -> 3)
    m2.run(m.getCurrentStateName)
    println(m2.getContext)
    m2.context("a") should be === 6
  }

  test("machine run with exception") {
    Bulbasaur.require()

    val m = Machine("process_with_exception")
    m.context_+("goto" -> 2)
    try {
      m.run()
      assert(false)
    } catch {
      case ex: Exception => logger.debug("get a exception: " + ex.getMessage, ex)
    }
    m.getCurrentStateName should be === "state2"

    val m2 = Machine("process_with_exception")
    m2.context_+("goto" -> 3)
    try {
      m2.run()
      assert(false)
    } catch {
      case ex: Exception => logger.debug("get a exception: " + ex.getMessage, ex)
    }
    m2.getCurrentStateName should be === "state1"
  }
}