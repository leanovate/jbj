package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, Expr}
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}

case class ReturnStmt(expr: Expr) extends Stmt {
  def exec(ctx: Context) = {
    ReturnExecResult(expr.eval(ctx))
  }
}
