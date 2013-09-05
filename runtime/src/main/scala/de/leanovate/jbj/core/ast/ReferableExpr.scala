/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.context.Context

trait ReferableExpr extends Expr {
  override def isDefined(implicit ctx: Context) = evalRef.isDefined

  def evalRef(implicit ctx: Context): Reference
}
