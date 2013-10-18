/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.{Expr, RefExpr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.core.ast.expr.BinaryRefExpr

case class ConcatWithRefExpr(reference: RefExpr, expr: Expr) extends BinaryRefExpr {
  override def eval(implicit ctx: Context) = reference.evalRef __= expr.eval

  override def phpStr = reference.phpStr + ".=" + expr.phpStr
}