package com.aixforce.bulbasaur.framework

import annotation.{Init, Optional, Named, Inject}
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-8-31
 */

class ContextTest extends FunSuite with ShouldMatchers {
  val context = new Context

  test("init module") {
    var s = "123"
    val module = new Module {
      override val configure = () => Array[Bind](
        bindClass(classOf[Bean3]) inject(Map("bean4" -> s)),
        bind(new Bean1),
        bind(new Bean2) annotatedWith "bean222",
        bind(new Bean2) annotatedWith "bean223",
        bind(Bean4)
      )
    }

    s = "345"

    context.init(List[Module](module))

    assert(context.getBeanByName("bean1") != None)
    assert(context.getBeanByName("bean222") != None)
    assert(context.getBeanByClass(classOf[Bean1]).size == 1)
    assert(context.getBeanByClass(classOf[Bean2]).size == 1)
    val bean3 = context.getBeanByName[Bean3]("bean3").get
    assert(bean3.bean1 != null)
    assert(bean3.bean2 != null)
    assert(bean3.bean3 == None)
    assert(bean3.bean223.get != null)
    assert(bean3.bean4 == Some("345"))
    println(Bean4.bean1)
  }
}

class Bean1 {
  @Init
  def init() {
    println("init bean1")
  }
}

class Bean2

class Bean3 {
  @Inject
  val bean1: Bean1 = null
  @Inject @Named("bean222")
  val bean2: Bean2 = null
  @Inject @Named @Optional(classOf[Bean2])
  val bean223: Option[Bean2] = None
  @Inject @Optional(classOf[String])
  val bean3: Option[String] = None
  @Inject @Named @Optional(classOf[String])
  val bean4: Option[String] = None

  @Init
  def init() {
    println("init bean3")
  }
}

object Bean4 {
  @Inject
  val bean1: Bean1 = null
}