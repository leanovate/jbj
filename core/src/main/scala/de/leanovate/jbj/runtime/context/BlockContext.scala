package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime.value.ValueRef
import de.leanovate.jbj.runtime.{Function, Context}

case class BlockContext(identifier: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, ValueRef]

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  val out = callerCtx.out

  val err = callerCtx.err

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

  def defineFunction(function: Function) {
    callerCtx.defineFunction(function)
  }
}
