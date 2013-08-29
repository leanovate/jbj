/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.context

import de.leanovate.jbj.core.runtime.PClass
import scala.collection.mutable
import de.leanovate.jbj.core.runtime.value.PVar

class ClassStaticContext(pClass: PClass, global: GlobalContext) extends StaticContext {
  private val parent: Option[StaticContext] = pClass.superClass.map(global.staticContext)

  private val variables = mutable.Map.empty[String, PVar]

  override def findVariable(name: String): Option[PVar] =
    variables.get(name).map(Some.apply).getOrElse {
      parent.flatMap(_.findVariable(name))
    }

  override def defineVariable(name: String, variable: PVar)(implicit ctx: Context) {
    variable.retain()
    variables.get(name).foreach(_.release())
    variables.put(name, variable)
  }

  override def undefineVariable(name: String)(implicit ctx: Context) {
    variables.remove(name).foreach(_.release())
  }

  def cleanup()(implicit ctx: Context) {
    variables.values.foreach(_.release())
  }
}
