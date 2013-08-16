package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.{NodePosition, NamespaceName}
import de.leanovate.jbj.runtime.value.{VarRef, PAnyVal, ObjectVal}
import de.leanovate.jbj.runtime._
import scala.collection.immutable.Stack

case class InstanceContext(instance: ObjectVal, callerPosition: NodePosition, callerCtx: Context) extends Context {
  lazy val global = callerCtx.global

  lazy val static = instance.pClass

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerPosition)

  def findConstant(name: String): Option[PAnyVal] = global.findConstant(name)

  def defineConstant(name: String, value: PAnyVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String)(implicit position: NodePosition): Option[VarRef] =
    instance.getProperty(name)(this).map {
      case valueRef: VarRef => valueRef
      case value: PAnyVal => VarRef(value)
    }

  def defineVariable(name: String, valueRef: VarRef)(implicit position: NodePosition) {
    instance.setProperty(name, valueRef.value)
  }

  def undefineVariable(name: String) {
    instance.unsetProperty(name)
  }

  def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }

}
