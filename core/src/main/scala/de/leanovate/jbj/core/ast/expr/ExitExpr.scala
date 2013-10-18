package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.ExitJbjException

case class ExitExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    throw new ExitJbjException(expr.eval.asVal.toStr.asString)
  }

  override def phpStr = "exit " + expr.phpStr
}
