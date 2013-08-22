package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.{NodePosition, NamespaceName}
import de.leanovate.jbj.runtime.value.{PVal, ObjectVal}
import de.leanovate.jbj.runtime._
import scala.collection.immutable.Stack

case class InstanceContext(instance: ObjectVal, callerCtx: Context) extends Context {
  lazy val global = callerCtx.global

  lazy val static = instance.pClass

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerCtx.currentPosition)

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  override def getVariable(name: String): Variable = Variable(name, this)

  protected[context] override def defineVariableInt(name: String, variable: Variable) {}

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }

  protected[context] override  def undefineVariableInt(name: String) {
  }
}
