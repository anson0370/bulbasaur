package test.spring

import org.springframework.context.support.ClassPathXmlApplicationContext
import io.Source

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-5-8
 */

object SpringTest {
  def main(args: Array[String]) {
//    val applicationContext = new ClassPathXmlApplicationContext(Array("test-spring-config.xml"))
//    applicationContext.getBeanDefinitionNames.foreach(println)
//
//    val componentClass2 = applicationContext.getBean("componentClass2").asInstanceOf[ComponentClass2]
//    componentClass2.test()

    var map = Map.empty[String, String]

    Source.fromFile("/Users/anson/Downloads/抢楼结果csv.csv", "GBK").getLines().foreach {
      line =>
        if (line.startsWith("论坛")) {
          println("重复结果")
        } else {
          val info = line.split(";")
          if (info.size < 5) {
            println("")
          } else {
            map.get(info(4)) match {
              case None =>
                map += (info(4) -> "%s %s %s".format(info(0), info(1), info(3)))
                println("")
              case Some(x) =>
                println("重复:" + x)
            }
          }
        }
    }
  }
}