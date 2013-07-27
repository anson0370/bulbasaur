package com.aixforce.bulbasaur.framework


/**
 * <p>
 *   Usage:
 *   <pre>
 *     class SomeModule extends Module {
 *       override val configure = List(
 *         bind classOf[TargetClass] to classOf[BeanClass],
 *         bind classOf[TargetClass2] annotatedWith "BeanName2" to classOf[BeanClass2],
 *         bind classOf[TargetClass3]
 *       )
 *     }
 *   </pre>
 * </p>
 *
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-6-11
 */
abstract class Module {

  val configure = () => Array.empty[Bind]

  val require = Array.empty[Module]
  val optionalRequire = Array.empty[Module]

  /* a reference friendly for Java */
  final val module = this

  /* will be true after init */
  final var initialized = false

  final def init() {
    afterInit()
    initialized = true
  }

  /**
   * will call before init any bean bind in this module
   * override this if need do something
   */
  def beforeInit() {}

  /**
   * will call after init all bean bind in this module
   * override this if need do something
   */
  def afterInit() {}

  def bind(obj: Any) = new Bind(Option(obj.asInstanceOf[AnyRef]), obj.asInstanceOf[AnyRef].getClass)

  def bindClass(clazz: Class[_]) = new Bind(None, clazz)
}