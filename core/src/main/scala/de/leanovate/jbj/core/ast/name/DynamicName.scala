/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.{NodeVisitor, Name, Expr}
import de.leanovate.jbj.runtime.context.Context

case class DynamicName(expr: Expr) extends Name {
  override def evalName(implicit ctx: Context) = expr.eval.asVal.toStr.asString

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr)
}
