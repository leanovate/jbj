package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.{Prog, NodePosition, NamespaceName}
import de.leanovate.jbj.runtime._
import scala.collection.mutable
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal, ObjectVal}

case class MethodContext(instance: ObjectVal, methodName: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, Variable]

  private val identifier = "Method_" + instance.pClass.name.toString + "::" + methodName

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerCtx.currentPosition)

  Variable("GLOBALS", this).value = global.GLOBALS
  Variable("this", this).value = instance

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String): Option[PVar] = {
    val variable = getVariable(name)
    if (variable.isDefined) {
      Some(variable)
    } else {
      None
    }
  }

  def defineVariable(name: String, pVar: PVar) {
    getVariable(name).ref = pVar
  }

  def undefineVariable(name: String) {
    localVariables.remove(name).foreach(_.cleanup())
  }

  override def getVariable(name: String): Variable = localVariables.getOrElse(name, Variable(name, this))

  protected[context] override def defineVariableInt(name: String, variable: Variable) {
    localVariables.get(name).foreach(_.cleanup())
    localVariables.put(name, variable)
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}

