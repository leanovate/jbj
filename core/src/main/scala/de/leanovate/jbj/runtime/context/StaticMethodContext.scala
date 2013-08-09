package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.{ValueRef, Value}
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.{PClass, PFunction, Context}
import scala.collection.mutable
import scala.collection.immutable.Stack

case class StaticMethodContext(pClass: PClass, methodName: String, callerPosition: NodePosition, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, ValueRef]

  private val identifier = "Class_" + pClass.name.toString

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerPosition)

  def findConstant(name: String): Option[Value] = global.findConstant(name)

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String)(implicit position: NodePosition): Option[ValueRef] =
    localVariables.get(name)

  def defineVariable(name: String, valueRef: ValueRef)(implicit position: NodePosition) {
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
