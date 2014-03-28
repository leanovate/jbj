package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.{Expr, RefExpr}
import de.leanovate.jbj.core.ast.expr.BinaryRefExpr
import de.leanovate.jbj.runtime.context.Context

case class BitXorWithRefExpr(reference: RefExpr, expr: Expr) extends BinaryRefExpr {
  override def eval(implicit ctx: Context) = reference.evalRef ^= expr.eval

  override def phpStr = reference.phpStr + "^=" + expr.phpStr
}
