package de.leanovate.jbj.exec

import java.io.PrintStream
import de.leanovate.jbj.ast.Value

trait Context {
  def out: PrintStream

  def getVariable(name: String): Value

  def setVariable(name: String, value: Value)
}
