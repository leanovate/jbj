/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.Name
import de.leanovate.jbj.runtime.context.Context

case class StaticName(name: String) extends Name {
  override def evalName(implicit ctx: Context) = name

  override def evalNameStrict(implicit ctx: Context) = Some(name)

  override def phpStr = name
}
