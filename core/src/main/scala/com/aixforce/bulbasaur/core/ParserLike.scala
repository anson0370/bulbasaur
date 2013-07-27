package com.aixforce.bulbasaur.core

import com.aixforce.bulbasaur.core.model.Process

trait ParserLike {
  def needRefresh(processName: String, processVersion: Int, oldProcess: Process): Boolean
  def parse(processName: String, processVersion: Int): Process
}