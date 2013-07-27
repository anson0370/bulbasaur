package com.aixforce.bulbasaur.framework

/**
 * <p>
 *   Priority:
 *    1. Named Annotation
 *    2. Annotation
 *    3. Target Class
 * </p>
 * @param obj bind target object
 *
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-6-22
 */

class Bind(val obj: Option[AnyRef], val clazz: Class[_]) {
  var name: Option[String] = None
  var beanClazz: Option[Class[_]] = None
  var injectMap: Map[String, Any] = Map.empty[String, Any]

  def to(beanClazz: Class[_]) = {
    this.beanClazz = Option(beanClazz)
    this
  }

  def annotatedWith(name: String) = {
    this.name = Option(name)
    this
  }

  def inject(map: Map[String, Any]) = {
    this.injectMap = map
    this
  }
}