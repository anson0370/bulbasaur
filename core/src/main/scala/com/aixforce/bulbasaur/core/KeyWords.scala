package com.aixforce.bulbasaur.core

object KeyWords {
  final val CURRENT_STATE_NAME = "currentStateName"
  final val CURRENT_PROCESS_NAME = "currentProcessName"

  private final val KEYWORDS = Array(CURRENT_STATE_NAME, CURRENT_PROCESS_NAME)

  def illegal(key: String) = {
    KEYWORDS.exists(key == _)
  }

  override def toString = {
    KEYWORDS.mkString(", ")
  }
}