package com.aixforce.bulbasaur.framework

import annotation.{Init, Optional, Named, Inject}
import collection.mutable.{Map => MutableMap}
import exception.{NoBeanMatchedException, DuplicationBeanException}
import com.aixforce.bulbasaur.helper.Logger
import com.aixforce.bulbasaur.helper.Helper.makeString

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-4-23
 */

class Context extends Logger {
  val namedBeanMap = MutableMap.empty[String, BeanProxy]
  val beanMap = MutableMap.empty[Class[_], List[BeanProxy]]

  def getBeanByName[T](name: String) = {
    namedBeanMap.get(name) match {
      case None => None
      case Some(beanProxy) => beanProxy.beanRef.asInstanceOf[Option[T]]
    }
  }

  def getBeanByClass[T](clazz: Class[T]) = {
    beanMap.get(clazz) match {
      case None => None
      case Some(beanProxyList) => Some(beanProxyList.map(_.beanRef)).asInstanceOf[Option[T]]
    }
  }

  def init(modules: List[Module]) {
    // order by require relation first
    val orderedModules = FrameworkUtil.orderModules(modules)
    // parse config and init each bean by config order
    // after init all bean , init the module itself
    orderedModules.foreach {
      module =>
        parseModuleConfigure(module).foreach(initBean(_))
        module.init()
    }
    if (logger.isDebugEnabled) {
      logger.debug("inited beans:[" + namedBeanMap.makeString(", ", (entry => entry._1 + " -> " + entry._2.beanRef)) + "]")
    }
  }

  private def parseModuleConfigure(module: Module) = {
    module.configure().foldLeft(List.empty[BeanProxy]) {
      (list, bind) =>
        val name = bind.name match {
          case None => FrameworkUtil.getBeanName(bind.clazz)
          case Some(beanName) => beanName
        }
        val beanProxy = new BeanProxy(bind)
        namedBeanMap.get(name) match {
          case None => namedBeanMap += (name -> beanProxy)
          case Some(existProxy) =>
            throw new DuplicationBeanException("duplication name:" + name + " for class[" +
              bind.clazz.getName + ", " + existProxy.beanClass.getName + "]")
        }

        val clazz = bind.beanClazz match {
          case None => bind.clazz
          case Some(beanClass) => beanClass
        }
        beanMap.get(clazz) match {
          case None => beanMap += (clazz -> List[BeanProxy](beanProxy))
          case Some(existProxyList) => beanMap += (clazz -> (beanProxy :: existProxyList))
        }
        beanProxy :: list
    }.reverse
  }

  private def initBean(beanProxy: BeanProxy): AnyRef = {
    if (beanProxy.injected) {
      beanProxy.beanRef.get
    } else {
      val beanRef = beanProxy.beanRef.getOrElse {
        val ref = beanProxy.beanClass.newInstance().asInstanceOf[AnyRef]
        beanProxy.beanRef = Option(ref)
        ref
      }
      beanProxy.beanClass.getDeclaredFields.withFilter {
        _.isAnnotationPresent(classOf[Inject])
      }.foreach(injectField(_, beanProxy))
      // trigger init action after inject
      initLifecycle(beanProxy)
      beanProxy.injected = true
      beanRef
    }
  }

  private def injectField(field: java.lang.reflect.Field, beanProxy: BeanProxy) {
    // no @Inject annotation, abort
    if (!field.isAnnotationPresent(classOf[Inject])) return
    // check if option
    val option = field.isAnnotationPresent(classOf[Optional])
    if (option && field.getType != classOf[Option[_]]) {
      throw new IllegalAccessException("field type should be Option when annotation by @Optional, field:" + field.getName + " class:" + beanProxy.beanClass.getName)
    }
    // @Named annotation, check namedBeanMap
    if (field.isAnnotationPresent(classOf[Named])) {
      val namedAnnotation = field.getAnnotation(classOf[Named])
      val beanName = if (namedAnnotation.value() == "") field.getName else namedAnnotation.value()
      // 找出目标bean 先找自己的injectMap里有没有 没有的话再去namedBeanMap里找
      val target = beanProxy.injectMap.get(beanName) match {
        case None =>
          namedBeanMap.get(beanName) match {
            case None =>
              if (!option) {
                throw new NoBeanMatchedException("no bean matched name:" + beanName)
              }
              None
            case Some(targetBeanProxy) =>
              Some(if (targetBeanProxy eq beanProxy) beanProxy.beanRef.get else initBean(targetBeanProxy))
          }
        case Some(any) =>
          Some(any)
      }
      target match {
        case None =>
          // do nothing
        case Some(targetRef) =>
          field.setAccessible(true)
          field.set(beanProxy.beanRef.get, if (option) Option(targetRef) else targetRef)
      }
    } else { // by class here
      val beanClass = if (option) field.getAnnotation(classOf[Optional]).value() else field.getType
      beanMap.get(beanClass) match {
        case None =>
          if (!option) {
            throw new NoBeanMatchedException("no bean matched class:" + beanClass)
          }
        case Some(beanProxyList) =>
          if (beanProxyList.size > 1) {
            throw new DuplicationBeanException("more than 1 bean matched class:" + beanClass + ", beans:" +
              beanProxyList.mkString("[", ",", "]"))
          } else {
            val targetBeanProxy = beanProxyList(0)
            val targetRef = ((anyRef: AnyRef) => if (option) Option(anyRef) else anyRef)(
                if (targetBeanProxy eq beanProxy) beanProxy.beanRef.get else initBean(targetBeanProxy)
              )
            field.setAccessible(true)
            field.set(beanProxy.beanRef.get, targetRef)
          }
      }
    }
  }

  private def initLifecycle(beanProxy: BeanProxy) {
    beanProxy.beanClass.getMethods.withFilter {
      _.isAnnotationPresent(classOf[Init])
    }.foreach(_.invoke(beanProxy.beanRef.get))
  }
}