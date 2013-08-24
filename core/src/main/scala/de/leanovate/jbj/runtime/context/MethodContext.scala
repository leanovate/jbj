package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.{NodePosition, NamespaceName}
import de.leanovate.jbj.runtime._
import scala.collection.mutable
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal, ObjectVal}

case class MethodContext(instance: ObjectVal, pClass: PClass, methodName: String, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Method_" + instance.pClass.name.toString + "::" + methodName

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerCtx.currentPosition)

  localVariables.put("GLOBALS", PVar(global.GLOBALS))
  localVariables.put("this", PVar(instance))

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

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}

