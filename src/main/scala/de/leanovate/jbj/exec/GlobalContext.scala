package de.leanovate.jbj.exec

import java.io.PrintStream
import de.leanovate.jbj.ast.Value
import scala.collection.mutable
import de.leanovate.jbj.ast.value.UndefinedVal

case class GlobalContext(out: PrintStream) extends Context {
  val variables = mutable.Map.empty[String, Value]

  def getVariable(name: String) = variables.getOrElse(name, UndefinedVal)

  def setVariable(name: String, value: Value) {
    variables.put(name, value)
  }

}
