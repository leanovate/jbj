package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.ast.{NoNodePosition, Prog, NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal}

case class FunctionContext(functionName: NamespaceName, callerPosition: NodePosition,
                           callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Function_" + functionName.toString

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

  def findVariable(name: String)(implicit position: NodePosition): Option[PVar] = localVariables.get(name)

  def defineVariable(name: String, pVar: PVar)(implicit position: NodePosition)  {
    localVariables.get(name).foreach(_.decrRefCount())
    localVariables.put(name, pVar)
    pVar.incrRefCount()
  }

  def undefineVariable(name: String)(implicit position: NodePosition) {
    localVariables.remove(name).foreach(_.decrRefCount())
  }

  def findFunction(name: NamespaceName) = global.findFunction(name)

  def defineFunction(function: PFunction) {
    global.defineFunction(function)
  }
}
