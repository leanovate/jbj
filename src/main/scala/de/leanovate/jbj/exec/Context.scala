package de.leanovate.jbj.exec

import java.io.PrintStream
import de.leanovate.jbj.ast.{Value, Function}

trait Context {
  def out: PrintStream

  def getVariable(name: String): Value

  def setVariable(name: String, value: Value)

  def findFunction(name: String): Option[Function]
}
