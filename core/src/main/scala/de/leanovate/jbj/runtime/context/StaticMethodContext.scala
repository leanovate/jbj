package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.{PVar, PVal}
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.{PClass, PFunction, Context}
import scala.collection.mutable
import scala.collection.immutable.Stack

case class StaticMethodContext(pClass: PClass, methodName: String, callerPosition: NodePosition, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Method_" + pClass.name.toString + "::" + methodName

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerPosition)

  localVariables.put("GLOBALS", PVar(global.GLOBALS))

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String)(implicit position: NodePosition): Option[PVar] =
    localVariables.get(name)

  def defineVariable(name: String, valueRef: PVar)(implicit position: NodePosition) {
    localVariables.get(name).foreach(_.decrRefCount())
    localVariables.put(name, valueRef)
    valueRef.incrRefCount()
  }

  def undefineVariable(name: String)(implicit position: NodePosition) {
    localVariables.remove(name).foreach(_.decrRefCount())
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}
