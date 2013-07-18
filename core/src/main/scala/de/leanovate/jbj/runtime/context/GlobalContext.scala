package de.leanovate.jbj.runtime.context

import java.io.PrintStream
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.ValueRef
import de.leanovate.jbj.runtime.{buildin, Function, Context}
import de.leanovate.jbj.runtime.Function

case class GlobalContext(out: PrintStream) extends Context {
  private val variables = mutable.Map.empty[String, ValueRef]

  private val functions = mutable.Map.empty[String, Function]

  private val staticContexts = mutable.Map.empty[String, StaticContext]

  def global = this

  def static = this

  def findVariable(name: String): Option[ValueRef] = variables.get(name)

  def defineVariable(name: String, valueRef: ValueRef) {
    variables.put(name, valueRef)
  }

  def findFunction(name: String) = buildin.buildinFunctions.get(name).map(Some(_)).getOrElse(functions.get(name))

  def defineFunction(function: Function) {
    functions.put(function.name, function)
  }

  def staticContext(identifier: String): StaticContext =
    staticContexts.getOrElseUpdate(identifier, new StaticContext(this))
}
