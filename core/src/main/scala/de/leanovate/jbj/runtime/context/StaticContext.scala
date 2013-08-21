package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.PVar

trait StaticContext {
  var initialized = false

  def findVariable(name: String): Option[PVar] = {
    val variable = getVariable(name)
    if (variable.isDefined) {
      Some(variable)
    } else {
      None
    }
  }

  def defineVariable(name: String, pVar: PVar) {
    getVariable(name).ref = pVar
  }

  def undefineVariable(name: String) {
    getVariable(name).unset()
  }

  def getVariable(name: String): StaticVariable = ???

  protected[context] def defineVariableInt(name:String, variable:StaticVariable) {}

  protected[context] def undefineVariableInt(name: String)
}
