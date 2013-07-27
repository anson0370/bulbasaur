package com.aixforce.bulbasaur.helper

import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 11-12-30
 */

trait Logger {
  protected val logger = LoggerFactory.getLogger(this.getClass)
}