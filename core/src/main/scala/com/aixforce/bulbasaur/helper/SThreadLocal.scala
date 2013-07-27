package com.aixforce.bulbasaur.helper

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-5-15
 */

class SThreadLocal[T](init: => T) extends ThreadLocal[T] {
  override def initialValue: T = init
  def apply = get
  def withValue[Y](func: T => Y): Y = func(get)
  def updateValue(func: T => T): T = {
    val newValue = func(get)
    set(newValue)
    newValue
  }
  def withUpdateValue[Y](func: T => T)(func2: T => Y): Y = {
    func2(updateValue(func))
  }
}