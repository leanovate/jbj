package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.ast.{Prog, NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal}

class GenericStaticContext(var global: GlobalContext) extends StaticContext {
  private val variables = mutable.Map.empty[String, StaticVariable]

  def findConstant(name: String): Option[PVal] = global.findConstant(name)

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    global.defineConstant(name, value, caseInsensitive)
  }

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
    variables.remove(name).foreach(_.cleanup())
  }

  override def getVariable(name: String): StaticVariable = variables.getOrElse(name, StaticVariable(name, this))

  protected[context] override def defineVariableInt(name: String, variable: StaticVariable) {
    variables.get(name).foreach(_.cleanup())
    variables.put(name, variable)
  }
}