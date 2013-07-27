package test.spring

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-5-8
 */

@Component
class ComponentClass2 {
  @Autowired
  val componentClass = __[ComponentClass]

  def test() {
    componentClass.test()
  }

  def __[T] = null.asInstanceOf[T]
}