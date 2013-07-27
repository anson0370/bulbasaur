package test

import scala.concurrent.ops._
import concurrent.TaskRunners
import java.util.concurrent.CountDownLatch

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-3-21
 */

object SpawnTest {
  val latch = new CountDownLatch(10000)

  def main(args: Array[String]) {
    val start = System.currentTimeMillis()
    (0 until 10000).foreach {
      i =>
        spawn {
          Thread.sleep(10 * (i % 4))
          m(i)
        }(TaskRunners.threadPoolRunner)
    }
    println("end--------------------------------------")
    latch.await()
    println("time:" + (System.currentTimeMillis() - start) + "ms")
    TaskRunners.threadPoolRunner.shutdown()
  }

  def m(i: Int) {
    println("Thread:" + Thread.currentThread().getName + " msg:" + i)
    latch.countDown()
  }
}