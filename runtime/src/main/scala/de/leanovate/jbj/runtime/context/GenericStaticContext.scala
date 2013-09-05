/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime.value.PVar

class GenericStaticContext extends StaticContext {
  private val _variables = mutable.Map.empty[String, PVar]

  override def variables: Map[String, PVar] = _variables.toMap

  override def findVariable(name: String): Option[PVar] = _variables.get(name)

  override def defineVariable(name: String, variable: PVar)(implicit ctx: Context) {
    variable.retain()
    _variables.get(name).foreach(_.release())
    _variables.put(name, variable)
  }

  override def undefineVariable(name: String)(implicit ctx: Context) {
    _variables.remove(name).foreach(_.release())
  }

  def cleanup()(implicit ctx: Context) {
    _variables.values.foreach(_.release())
  }
}