package de.leanovate.jbj.exec

import de.leanovate.jbj.ast.{Function, Value}
import scala.collection.mutable
import scala.annotation.tailrec
import de.leanovate.jbj.ast.value.ValueRef

case class BlockContext(identifier: String, callerCtx: Context) extends Context {
  val localVariables = mutable.Map.empty[String, ValueRef]

  val out = callerCtx.out

  def findVariable(name: String): Option[ValueRef] =
    localVariables.get(name) match {
      case value@Some(_) => value
      case None => callerCtx.findVariable(name)
    }

  def defineVariable(name: String, static: Boolean, valueRef: ValueRef) {
    localVariables.put(name, valueRef)
  }

  def findFunction(name: String) = callerCtx.findFunction(name)

  def defineFunction(function: Function, static: Boolean) {
    callerCtx.defineFunction(function, static)
  }
}
