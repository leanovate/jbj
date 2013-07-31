package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime.{ValueRef, Value, PFunction, Context}
import de.leanovate.jbj.ast.NamespaceName

case class FunctionContext(identifier: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, ValueRef]

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  val out = callerCtx.out

  val err = callerCtx.err

  def findClass(name: NamespaceName): Option[ClassContext] = global.findClass(name)

  def defineClass(name: String): ClassContext = global.defineClass(name)

  def findConstant(name: String): Option[Value] = global.findConstant(name)

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String): Option[ValueRef] = localVariables.get(name)

  def defineVariable(name: String, valueRef: ValueRef) {
    localVariables.put(name, valueRef)
  }

  def undefineVariable(name: String) {
    localVariables.remove(name)
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}
