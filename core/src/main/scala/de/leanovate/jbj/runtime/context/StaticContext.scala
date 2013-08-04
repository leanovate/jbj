package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.ast.{NodePosition, NamespaceName}
import scala.collection.immutable.Stack

class StaticContext(var global: GlobalContext) extends Context {
  private val variables = mutable.Map.empty[String, ValueRef]

  var initialized = false

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
  def findClass(name: NamespaceName): Option[PClass] = global.findClass(name)

  def defineClass(pClass: PClass) {
    global.defineClass(pClass)
  }
}