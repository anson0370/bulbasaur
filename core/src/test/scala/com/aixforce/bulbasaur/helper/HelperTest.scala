package com.aixforce.bulbasaur.helper

import java.io.File
import java.net.JarURLConnection
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-2-8
 */

class HelperTest extends FunSuite with ShouldMatchers {

  test("scan package") {
    val basePackage = "com.tmall.pokemon"
    val resources = Thread.currentThread().getContextClassLoader.getResources(basePackage.replace('.', '/'))
    var result = List.empty[Class[_]]
    while (resources.hasMoreElements) {
      val url = resources.nextElement()
      url.getProtocol match {
        case "jar" =>
          val jarEntries = url.openConnection().asInstanceOf[JarURLConnection].getJarFile.entries()
          while (jarEntries.hasMoreElements) {
            val jarEntry = jarEntries.nextElement()
            if (jarEntry.getName.endsWith(".class") && jarEntry.getName.indexOf('$') == -1) {
              result = Class.forName(jarEntry.getName.replace('/', '.').dropRight(6)) :: result
            }
          }
        case "file" =>
          val rootDirectory = new File(url.getFile);
          def recursionGetClass(packageName: String, directory: File) {
            directory.listFiles().foreach {
              file =>
                if (file.isDirectory) {
                  recursionGetClass(packageName + "." + file.getName, file)
                } else {
                  if (file.getName.endsWith(".class") && file.getName.indexOf('$') == -1) {
                    result = Class.forName(packageName + "." + file.getName.dropRight(6)) :: result
                  }
                }
            }
          }
          recursionGetClass(basePackage, rootDirectory)
      }
    }
    result.withFilter(!_.getAnnotations.isEmpty).foreach {
      e =>
        println(e)
        e.getAnnotations.foreach(a => print("|" + a.getClass.getName))
        println("======")
    }
  }
}