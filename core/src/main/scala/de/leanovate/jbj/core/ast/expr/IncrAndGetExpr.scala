/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.Reference.++

case class IncrAndGetExpr(reference: ReferableExpr) extends Expr {
  override def eval(implicit ctx: Context) = ++(reference.evalRef)
}
