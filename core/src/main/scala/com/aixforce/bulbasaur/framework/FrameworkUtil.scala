package com.aixforce.bulbasaur.framework

import collection.mutable.{Map => MMap}
import com.aixforce.bulbasaur.helper.Logger


/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-8-31
 */

object FrameworkUtil extends Logger {
  def getBeanName(clazz: Class[_]): String = {
    val simpleName = clazz.getSimpleName
    simpleName.head.toLower + simpleName.tail
  }

  def orderModules(modules: List[Module]): List[Module] = {
    // (class -> (instance, priorityCount))
    val dependencies = MMap.empty[Module, Int]
    modules.foreach(module => checkRequire(module, List.empty[Module], dependencies))
    dependencies.keys.foreach(module => calcRequire(module, dependencies))
    dependencies.toList.sortWith((d1, d2) => d1._2 >= d2._2).foldLeft(List.empty[Module]) {
      (result, entry) =>
        val (module, priority) = entry
        if (logger.isDebugEnabled) logger.debug("ordered module: " + module.getClass.getSimpleName + ", priority: " + priority)
        module :: result
    }.reverse
  }

  private def checkRequire(module: Module,
                           checkModuleList: List[Module],
                           dependencies: MMap[Module, Int]) {
    // check cycle dependency
    Predef.require(!checkModuleList.exists(module == _), "find cycle dependency at " + module.getClass.getSimpleName)
    dependencies.getOrElseUpdate(module, 0)
    module.require.foreach {
      requireModule =>
        checkRequire(requireModule, module :: checkModuleList, dependencies)
    }
  }

  private def calcRequire(module: Module,
                          dependencies: MMap[Module, Int]) {
    val requireList = module.require.toList ::: module.optionalRequire.filter(dependencies contains).toList
    requireList.foreach {
      requireModule =>
        val requirePriorityCount = dependencies(requireModule)
        // calc priorityCount
        dependencies += (requireModule -> (requirePriorityCount + 1))
        calcRequire(requireModule, dependencies)
    }
  }
}