/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.value.{ArrayLike, NullVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class IndexGetExpr(expr: Expr, indexExpr: Option[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = {
    if (indexExpr.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")

    expr.eval.asVal match {
      case array: ArrayLike => array.getAt(indexExpr.get.eval.asVal).map(_.asVal).getOrElse(NullVal)
      case _ => NullVal
    }
  }

  override def phpStr = expr.phpStr + indexExpr.map("[" + _.phpStr + "]").getOrElse("[]")
}
