package de.leanovate.jbj.core.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.core.runtime.value.{PVar, PVal}

class GenericStaticContext extends StaticContext {
  private val variables = mutable.Map.empty[String, PVar]

  override def findVariable(name: String): Option[PVar] = variables.get(name)

  override def defineVariable(name: String, variable: PVar)(implicit ctx: Context) {
    variable.retain()
    variables.get(name).foreach(_.release())
    variables.put(name, variable)
  }

  override def undefineVariable(name: String)(implicit ctx: Context)  {
    variables.remove(name).foreach(_.release())
  }

  def cleanup()(implicit ctx: Context) {
    variables.values.foreach(_.release())
  }
}