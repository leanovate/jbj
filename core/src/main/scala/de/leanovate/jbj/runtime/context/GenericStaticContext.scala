package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime.value.PVal

class GenericStaticContext(var global: GlobalContext) extends StaticContext {
  private val variables = mutable.Map.empty[String, StaticVariable]

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  override def getVariable(name: String): StaticVariable = variables.getOrElse(name, StaticVariable(name, this))

  protected[context] override def defineVariableInt(name: String, variable: StaticVariable) {
    variables.get(name).foreach(_.cleanup())
    variables.put(name, variable)
  }

  protected[context] override def undefineVariableInt(name: String) {
    variables.remove(name).foreach(_.cleanup())
  }
}