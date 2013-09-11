/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.context.Context

case class DimReferableExpr(reference: ReferableExpr, indexExpr: Option[Expr]) extends ReferableExpr {
  override def evalRef(implicit ctx: Context) =
    indexExpr.map {
      idx => reference.evalRef.dim(idx.eval)
    }.getOrElse {
      reference.evalRef.dim()
    }

  override def eval(implicit ctx: Context) = evalRef.byVal
}
