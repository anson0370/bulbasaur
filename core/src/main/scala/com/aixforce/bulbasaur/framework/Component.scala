package com.aixforce.bulbasaur.framework

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-5-31
 */

trait Component {
  {
    // add self to context
  }

  /**
   * init method
   * @tparam T Type
   * @return Type T with null value
   */
  def __[T] = null.asInstanceOf[T]
}