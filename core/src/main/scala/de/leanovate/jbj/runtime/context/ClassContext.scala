package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.{Prog, NodePosition, NamespaceName}
import de.leanovate.jbj.runtime.value.{Value, ObjectVal}
import de.leanovate.jbj.runtime._
import scala.collection.immutable.Stack

case class ClassContext(instance: ObjectVal, callerPosition: NodePosition, callerCtx: Context) extends Context {
  private val identifier = "Class_" + instance.pClass.name.toString

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
    instance.getProperty(name)(this, position).map(ValueRef(_))

  def defineVariable(name: String, valueRef: ValueRef)(implicit position: NodePosition) {
    instance.setProperty(name, valueRef.value)(this, position)
  }

  def undefineVariable(name: String) {
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }

}
