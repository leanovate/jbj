package de.leanovate.jbj.exec

import de.leanovate.jbj.ast.{Function, Value}
import scala.collection.mutable
import scala.annotation.tailrec
import de.leanovate.jbj.ast.value.ValueRef

case class BlockContext(identifier: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, ValueRef]

  lazy val global = callerCtx.global

  val out = callerCtx.out

  def findVariable(name: String): Option[ValueRef] =
    localVariables.get(name) match {
      case None => global.findStaticVariable(identifier, name) match {
        case None => callerCtx.findVariable(name)
        case staticVar => staticVar
      }
      case localVar => localVar
    }

  def defineVariable(name: String, static: Boolean, valueRef: ValueRef) {
    if (static)
      global.defineStaticVariable(identifier, name, valueRef)
    else
      localVariables.put(name, valueRef)
  }

  def findFunction(name: String) = callerCtx.findFunction(name)

  def defineFunction(function: Function, static: Boolean) {
    callerCtx.defineFunction(function, static)
  }
}
