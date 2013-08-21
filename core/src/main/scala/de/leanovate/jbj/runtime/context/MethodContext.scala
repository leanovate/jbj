package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.{Prog, NodePosition, NamespaceName}
import de.leanovate.jbj.runtime._
import scala.collection.mutable
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal, ObjectVal}

case class MethodContext(instance: ObjectVal, methodName: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Method_" + instance.pClass.name.toString + "::" + methodName

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

  def findVariable(name: String): Option[PVar] =
    if (name == "this") Some(PVar(instance)) else localVariables.get(name)

  def defineVariable(name: String, valueRef: PVar)  {
    localVariables.get(name).foreach(_.cleanup())
    localVariables.put(name, valueRef)
  }

  def undefineVariable(name: String) {
    localVariables.remove(name).foreach(_.cleanup())
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}

