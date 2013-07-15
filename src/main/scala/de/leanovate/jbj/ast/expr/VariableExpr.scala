package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.Context

case class VariableExpr(variableName: String) extends Expr {
  def eval(ctx: Context) = ctx.getVariable(variableName)
}
