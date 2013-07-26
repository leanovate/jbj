package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime.{Value, PFunction, Context}
import de.leanovate.jbj.runtime.value.ValueRef

class StaticContext(var global: GlobalContext) extends Context {
  private val variables = mutable.Map.empty[String, ValueRef]

  def static = this

  def out = global.out

  def err = global.err

  def findClass(name: String): Option[ClassContext] = global.findClass(name)

  def defineClass(name: String): ClassContext = global.defineClass(name)

  def findConstant(name: String): Option[Value] = global.findConstant(name)

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  def findVariable(name: String) = variables.get(name)

  def defineVariable(name: String, valueRef: ValueRef) {
    variables.put(name, valueRef)
  }

  def findFunction(name: String) = global.findFunction(name)

  def defineFunction(function: PFunction) {
    global.defineFunction(function)
  }
}