package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime.value.ValueRef
import de.leanovate.jbj.runtime.{Value, PFunction, Context}

case class BlockContext(identifier: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, ValueRef]

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  val out = callerCtx.out

  val err = callerCtx.err

  def findClass(name: String): Option[ClassContext] = global.findClass(name)

  def defineClass(name: String): ClassContext = global.defineClass(name)

  def findConstant(name: String): Option[Value] = global.findConstant(name)

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String): Option[ValueRef] =
    localVariables.get(name) match {
      case None => static.findVariable(name) match {
        case None => callerCtx.findVariable(name)
        case staticVar => staticVar
      }
      case localVar => localVar
    }

  def defineVariable(name: String, valueRef: ValueRef) {
    localVariables.put(name, valueRef)
  }

  def findFunction(name: String) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}
