package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.runtime.value.ValueRef

trait Context {
  def global: GlobalContext

  def out: PrintStream

  def findVariable(name: String): Option[ValueRef]

  def defineVariable(name: String, static: Boolean, valueRef: ValueRef)

  def findFunction(name: String): Option[Function]

  def defineFunction(function: Function, static: Boolean)
}
