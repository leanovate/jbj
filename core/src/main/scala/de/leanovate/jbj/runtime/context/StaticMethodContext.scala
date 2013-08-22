package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.{PClass, PFunction}
import scala.collection.mutable
import scala.collection.immutable.Stack

case class StaticMethodContext(pClass: PClass, methodName: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, Variable]

  private val identifier = "Method_" + pClass.name.toString + "::" + methodName

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerCtx.currentPosition)

  Variable("GLOBALS", this).value = global.GLOBALS

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  override def getVariable(name: String): Variable = localVariables.getOrElse(name, Variable(name, this))

  protected[context] override def defineVariableInt(name: String, variable: Variable) {
    localVariables.get(name).foreach(_.cleanup())
    localVariables.put(name, variable)
  }

  protected[context] override def undefineVariableInt(name: String) {
    localVariables.remove(name).foreach(_.cleanup())
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}
