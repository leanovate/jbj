package de.leanovate.jbj.exec

import java.io.PrintStream
import de.leanovate.jbj.ast.Value
import scala.collection.mutable
import de.leanovate.jbj.ast.value.ValueRef
import de.leanovate.jbj.ast.buildin

case class GlobalContext(out: PrintStream) extends Context {
  val variables = mutable.Map.empty[String, ValueRef]

  val staticVariables = mutable.Map.empty[(String, String), ValueRef]

  def findVariable(name: String): Option[ValueRef] = variables.get(name)

  def defineVariable(name: String, static: Boolean, value: Value) = value match {
    case valueRef: ValueRef => variables.put(name, valueRef)
    case v => variables.put(name, new ValueRef(v))
  }

  def findFunction(name: String) = buildin.buildinFunctions.get(name)
}
