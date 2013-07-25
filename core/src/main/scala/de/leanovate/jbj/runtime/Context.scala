package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.runtime.value.ValueRef
import de.leanovate.jbj.runtime.context.GlobalContext

trait Context {
  def global: GlobalContext

  def static: Context

  def out: PrintStream

  def err: PrintStream

  lazy val log: Log = new Log(out, err)

  def findConstant(name: String): Option[Value]

  def defineConstant(name: String, value: Value, caseInsensitive:Boolean)

  def findVariable(name: String): Option[ValueRef]

  def defineVariable(name: String, valueRef: ValueRef)

  def findFunction(name: String): Option[Function]

  def defineFunction(function: Function)
}
