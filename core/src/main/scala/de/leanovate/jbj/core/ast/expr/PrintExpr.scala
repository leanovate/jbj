/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr}
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.context.Context

case class PrintExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.out.print(expr.eval.toOutput)
    IntegerVal(1)
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr)

  override def phpStr = "print(" + expr.phpStr + ")"
}
