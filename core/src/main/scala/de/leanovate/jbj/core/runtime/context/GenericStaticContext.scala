package de.leanovate.jbj.core.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.core.runtime.value.{PVar, PVal}

class GenericStaticContext(var global: GlobalContext) extends StaticContext {
  private val variables = mutable.Map.empty[String, PVar]

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

  override def findVariable(name: String): Option[PVar] = variables.get(name)

  override def defineVariable(name: String, variable: PVar) {
    variable.retain()
    variables.get(name).foreach(_.release())
    variables.put(name, variable)
  }

  override def undefineVariable(name: String) {
    variables.remove(name).foreach(_.release())
  }

  def cleanup() {
    variables.values.foreach(_.release())
  }
}