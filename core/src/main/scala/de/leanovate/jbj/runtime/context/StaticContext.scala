package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime.{Function, Context}
import de.leanovate.jbj.runtime.value.ValueRef

class StaticContext(var global: GlobalContext) extends Context {
  private val variables = mutable.Map.empty[String, ValueRef]

  def static = this

  def out = global.out

  def err = global.err

  def findVariable(name: String) = variables.get(name)

  def defineVariable(name: String, valueRef: ValueRef) {
    variables.put(name, valueRef)
  }

  def findFunction(name: String) = global.findFunction(name)

  def defineFunction(function: Function) {
    global.defineFunction(function)
  }
}