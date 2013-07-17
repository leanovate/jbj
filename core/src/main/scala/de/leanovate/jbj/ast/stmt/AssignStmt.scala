package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.{SuccessExecResult, Context}
import de.leanovate.jbj.ast.value.ValueRef

case class AssignStmt(variableName: String, expr: Expr, static: Boolean) extends Stmt {
  override def exec(ctx: Context) = {
    val value = expr.eval(ctx)
    ctx.findVariable(variableName) match {
      case Some(valueRef) => valueRef.value = value.copy
      case None => ctx.defineVariable(variableName, static, ValueRef(value.copy))
    }
    SuccessExecResult()
  }
}
