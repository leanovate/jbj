package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.Context

case class AssignStmt(variableName: String, expr: Expr) extends Stmt {
  override def exec(ctx: Context) {
    ctx.setVariable(variableName, expr.eval(ctx))
  }
}
