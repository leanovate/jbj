package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}

case class ReturnStmt(position: FilePosition, expr: Expr) extends Stmt {
  def exec(ctx: Context) = {
    ReturnExecResult(expr.eval(ctx))
  }
}
