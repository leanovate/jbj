package de.leanovate.jbj.exec

import java.io.PrintStream
import de.leanovate.jbj.ast.{Function, Value, buildin}
import scala.collection.mutable
import de.leanovate.jbj.ast.value.ValueRef

case class GlobalContext(out: PrintStream) extends Context {
  private val variables = mutable.Map.empty[String, ValueRef]

  private val staticVariables = mutable.Map.empty[(String, String), ValueRef]

  private val functions = mutable.Map.empty[String, Function]

  def global = this

  def findVariable(name: String): Option[ValueRef] = variables.get(name)

  def defineVariable(name: String, static: Boolean, valueRef: ValueRef) {
    variables.put(name, valueRef)
  }

  def findStaticVariable(namespace: String, name: String): Option[ValueRef] = staticVariables.get(namespace -> name)

  def defineStaticVariable(namespace: String, name: String, valueRef: ValueRef) {
    staticVariables.put(namespace -> name, valueRef)
  }

  def findFunction(name: String) = buildin.buildinFunctions.get(name).map(Some(_)).getOrElse(functions.get(name))

  def defineFunction(function: Function, static: Boolean) {
    functions.put(function.name, function)
  }
}
