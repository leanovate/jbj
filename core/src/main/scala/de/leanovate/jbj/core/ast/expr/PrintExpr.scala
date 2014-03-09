/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.Operators._

case class PrintExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    print(expr.eval)
  }

  override def accept[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr)

  override def phpStr = "print(" + expr.phpStr + ")"
}
