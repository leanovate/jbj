package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class ExprStmt(expr:Expr) extends Stmt {
  def exec(ctx: Context) = {
    expr.eval(ctx)
    SuccessExecResult()
  }
}
