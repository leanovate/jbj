package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.{Prog, NodePosition, NamespaceName}
import de.leanovate.jbj.runtime._
import scala.collection.mutable
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal, ObjectVal}

case class MethodContext(instance: ObjectVal, methodName: String, callerPosition: NodePosition, callerCtx: Context) extends Context {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Method_" + instance.pClass.name.toString + "::" + methodName

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerPosition)

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String)(implicit position: NodePosition): Option[PVar] =
    if (name == "this") Some(PVar(instance)) else localVariables.get(name)

  def defineVariable(name: String, valueRef: PVar)(implicit position: NodePosition)  {
    localVariables.get(name).foreach(_.decrRefCount())
    localVariables.put(name, valueRef)
    valueRef.incrRefCount()
  }

  def undefineVariable(name: String) {
    localVariables.remove(name).foreach(_.decrRefCount())
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }
}

