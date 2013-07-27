package test.spring

import org.springframework.stereotype.Component

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-5-8
 */

@Component
class ComponentClass {
  var s: String = "default"

  def this(s1: String) = {
    this()
    s = s1
  }

  def test() {
    println(s)
  }
}