package de.leanovate.jbj.exec

import java.io.PrintStream
import de.leanovate.jbj.ast.{Function, Value, buildin}
import scala.collection.mutable
import de.leanovate.jbj.ast.value.ValueRef

case class GlobalContext(out: PrintStream) extends Context {
  val variables = mutable.Map.empty[String, ValueRef]

  val staticVariables = mutable.Map.empty[(String, String), ValueRef]

  val functions = mutable.Map.empty[String, Function]

  def findVariable(name: String): Option[ValueRef] = variables.get(name)

  def defineVariable(name: String, static: Boolean, valueRef: ValueRef) {
    variables.put(name, valueRef)
  }

  def findFunction(name: String) = buildin.buildinFunctions.get(name).map(Some(_)).getOrElse(functions.get(name))

  def defineFunction(function: Function, static: Boolean) {
    functions.put(function.name, function)

  }
}
