package com.aixforce.bulbasaur.core

import model.StateLike
import scala.collection.Map
import scala.collection.JavaConversions.collectionAsScalaIterable

import com.aixforce.bulbasaur.helper.{Helper, Logger}

class Machine protected(processName: String, processVersion: Int) extends Logger {
  protected val process = Place(processName, processVersion)
  protected var currentStateName = process.first
  protected val _context = scala.collection.mutable.Map.empty[String, Any]

  {
    if (logger.isDebugEnabled) logger.debug("create new machine with process: " + process)

    // init context
    _context += (KeyWords.CURRENT_PROCESS_NAME -> process.name)
    _context += (KeyWords.CURRENT_STATE_NAME -> currentStateName)
  }

  ////////////////////////////////
  // method for runtime info start
  ////////////////////////////////

  /**
   * @return name of this process in machine
   */
  def getProcessName = process.name

  /**
   * @return real version of this process in machine
   */
  def getProcessVersion = process.version

  /**
   * @return current state name of this process instance in machine
   */
  def getCurrentStateName = currentStateName

  ////////////////////////////////
  // method for context start
  ////////////////////////////////

  /**
   * <b>Friendly to Java</b>
   * <br/>
   * add a key-value pair into context, replace if key exist
   *
   * @param key parameter name
   * @param value parameter value
   */
  def addContext(key: String, value: Any) {
    context_+((key, value))
  }

  /**
   * <b>Friendly to Java</b>
   * <p>
   *   add a key-value map into context, replace if key exist
   * </p>
   *
   * @param context Java map
   */
  def addContext(context: java.util.Map[String, Any]) {
    context.keySet().foreach {
      key =>
        addContext(key, context.get(key))
    }
  }

  def context_+(elem: (String, Any)) {
    validKey(elem._1)
    _context += (elem)
  }

  def context_+(elem1: (String, Any), elem2: (String, Any), elems: (String, Any)*) {
    validKey(elem1._1)
    validKey(elem2._1)
    elems.foreach(elem => validKey(elem._1))
    _context += elem1 += elem2 ++= elems
  }

  def context_++(elems: TraversableOnce[(String, Any)]) {
    elems.foreach(elem => validKey(elem._1))
    _context ++= elems
  }

  def context_-(key: String) {
    _context -= key
  }

  /**
   * <b>Friendly to Java</b>
   * <br/>
   * remove a parameter
   *
   * @param key parameter name
   * @return value of the parameter removed, null if not exist
   */
  def removeContext(key: String) = {
    _context.remove(key).getOrElse(null)
  }

  /**
   * <b>Friendly to Java</b>
   * <br/>
   * get a parameter
   *
   * @param key parameter name
   * @return
   */
  def context(key: String) = {
    _context(key)
  }

  /**
   * <b>Friendly to Java</b>
   * <br/>
   * get a context clone with java.util.Map
   *
   * @return context clone with class of java.util.Map
   */
  def getContextClone = {
    Helper.copy2JavaMap(_context)
  }

  def getContext = {
    _context.clone()
  }

  //////////////////////////////
  // method for context end
  //////////////////////////////

  /**
   * run from current state
   */
  def run() {
    run(currentStateName)
  }

  /**
   * run from given state
   *
   * @param stateName state name
   */
  def run(stateName: String) {
    if (CoreModule.isShutdown) {
      throw new IllegalStateException("pikachu is shutdown now, no more machine request allowed")
    }
    var currentState = run0_findCurrent(stateName)
    while (currentState != None) {
      try {
        currentState = run_atom(currentState.get) match {
          case (false, _) => None
          case (true, Some(nextState)) => Some(nextState)
        }
      } catch {
        // if any exception is thrown, rollback to last step
        case ex: Exception => {
          currentStateName = currentState.get.name
          _context += (KeyWords.CURRENT_STATE_NAME -> currentStateName)
          throw ex
        }
      }
    }
  }

  protected def run_atom(currentState: StateLike): (Boolean, Option[StateLike]) = {
    run0(currentState)
  }

  protected def run0(currentState: StateLike): (Boolean, Option[StateLike]) = {
    // step1: run current state's execute method
    if (!run0_execute(currentState)) {
      // execute return false, return (false, None) to suspend process
      (false, None)
    } else {
      // step2: calc next state
      run0_calcNext(currentState) match {
        case None => {
          if (logger.isInfoEnabled) logger.info(process.toString + " have no next state after " + currentStateName + ", end")
          (false, None)
        }
        case Some(nextState) => {
          // if next state there, update current state to next state, run next state's prepare method
          currentStateName = nextState.name
          _context += (KeyWords.CURRENT_STATE_NAME -> currentStateName)
          (run0_prepare(nextState), Some(nextState))
        }
      }
    }
  }

  protected def run0_prepare(current: StateLike): Boolean = {
    current.prepare(_context) match {
      case (isSuccess, incrMap) => {
        mergeContext(incrMap)
        isSuccess
      }
    }
  }

  protected def run0_execute(current: StateLike): Boolean = {
    current.execute(_context) match {
      case (isSuccess, incrMap) => {
        mergeContext(incrMap)
        isSuccess
      }
    }
  }

  protected def run0_findCurrent(currentName: String): Option[StateLike] = {
    process.getState(currentName).orElse {
      throw new NullPointerException("no state find for name:" + currentName + " in " + process)
    }
  }

  protected def run0_calcNext(current: StateLike): Option[StateLike] = {
    current.willGo(_context) match {
      case None => None
      case Some(nextName) => process.getState(nextName).orElse {
        throw new NullPointerException("no state find for name:" + nextName + " in " + process)
      }
    }
  }

  private def mergeContext(incrMap: Map[String, Any]) {
    if (!(incrMap == null) && !incrMap.eq(_context) && !incrMap.isEmpty) {
      _context ++= incrMap
    }
  }

  private def validKey(key: String) {
    require(!KeyWords.illegal(key), "[" + key + "] is a KEYWORD in Machine, choose another one. KEYWORD:[" + KeyWords + "]")
  }

  override def toString = "%s | %s-%d(%s)".format(super.toString, processName, processVersion, currentStateName)
}

object Machine {
  def apply(processName: String): Machine = {
    apply(processName, 0)
  }

  def apply(processName: String, processVersion: Int): Machine = {
    new Machine(processName, processVersion)
  }
}