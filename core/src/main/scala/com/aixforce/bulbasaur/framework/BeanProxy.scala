package com.aixforce.bulbasaur.framework

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-8-30
 */

class BeanProxy(val bind: Bind) {
  var injected = false
  var beanClass = bind.clazz
  var beanRef: Option[AnyRef] = bind.obj
  var injectMap = bind.injectMap

  override def toString = beanClass.getName + "|" + beanRef.getOrElse("null").toString
}