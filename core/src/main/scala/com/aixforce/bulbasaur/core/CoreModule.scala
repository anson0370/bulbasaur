package com.aixforce.bulbasaur.core

import model._
import org.springframework.context.ApplicationContext
import reflect.BeanProperty
import com.aixforce.bulbasaur.helper.Logger
import com.aixforce.bulbasaur.framework.{Bind, Module}

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 11-12-29
 */

object CoreModule extends Module with Logger {

  override def afterInit() {
    applyState(classOf[Start], classOf[State], classOf[Event])
    logger.info("Module Core initialized")
  }
  /**
   * spring context
   */
  @BeanProperty var springContext: ApplicationContext = _
  /**
   * sign for app or environment
   */
  @BeanProperty var ownSign: String = "default"

  var isShutdown: Boolean = false

  def applyState(stateClass: Class[_ <: StateLike]) {
    if (stateClass.isAnnotationPresent(classOf[StateMeta])) {
      Parser.stateMap += (stateClass.getAnnotation(classOf[StateMeta]).t() -> stateClass)
    }
  }

  def applyState(stateLabel: String, stateClass: Class[_ <: StateLike]) {
    applyState((stateLabel, stateClass))
  }

  def applyState(state: (String, Class[_ <: StateLike])) {
    Parser.stateMap += state
  }

  def applyState(stateClass1: Class[_ <: StateLike], stateClasses: Class[_ <: StateLike]*) {
    applyState(stateClass1)
    stateClasses.foreach(applyState(_))
  }

  def applyState(state1: (String, Class[_ <: StateLike]), states: (String, Class[_ <: StateLike])*) {
    Parser.stateMap += state1
    Parser.stateMap ++= states
  }

  def setParser(parser: ParserLike) {
    Place.parser = parser
  }

  /**
   * 调用此方法后任何对Machine.run的调用都会抛出IllegalStateException，以此来阻止新流程继续被运行。
   */
  def shutdown() {
    isShutdown = true
  }
}