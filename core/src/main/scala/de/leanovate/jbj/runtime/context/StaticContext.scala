package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.PVar

trait StaticContext {
  var initialized = false

  def findVariable(name: String):Option[PVar]

  def defineVariable(name: String, valueRef: PVar)

  def undefineVariable(name: String)

  def getVariable(name: String): StaticVariable = ???

  protected[context] def defineVariableInt(name:String, variable:StaticVariable) {}
}
