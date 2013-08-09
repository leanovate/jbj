package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.ast.{Prog, NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{ValueRef, Value}

class GenericStaticContext(var global: GlobalContext) extends Context with StaticContext {
  private val variables = mutable.Map.empty[String, ValueRef]

  def static = this

  lazy val settings = global.settings

  def out = global.out

  def err = global.err

  def stack: Stack[NodePosition] = Stack.empty[NodePosition]

  def findConstant(name: String): Option[Value] = global.findConstant(name)

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String)(implicit position: NodePosition) = variables.get(name)

  def defineVariable(name: String, valueRef: ValueRef)(implicit position: NodePosition)  {
    variables.put(name, valueRef)
  }

  def undefineVariable(name: String) {
    variables.remove(name)
  }

  def findFunction(name: NamespaceName) = global.findFunction(name)

  def defineFunction(function: PFunction) {
    global.defineFunction(function)
  }
}