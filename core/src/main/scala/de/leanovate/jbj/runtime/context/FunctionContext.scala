package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.ast.{NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal}

case class FunctionContext(functionName: NamespaceName,
                           callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Function_" + functionName.toString

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerCtx.currentPosition)

  localVariables.put("GLOBALS", PVar(global.GLOBALS))

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  override def findVariable(name: String): Option[PVar] = localVariables.get(name)

  override def defineVariable(name: String, variable: PVar) {
    variable.retain()
    localVariables.get(name).foreach(_.release())
    localVariables.put(name, variable)

  }

  override def undefineVariable(name: String) {
    localVariables.remove(name).foreach(_.release())
  }

  def findFunction(name: NamespaceName) = global.findFunction(name)

  def defineFunction(function: PFunction) {
    global.defineFunction(function)
  }

  def cleanup() {
    localVariables.values.foreach(_.release())
  }
}
