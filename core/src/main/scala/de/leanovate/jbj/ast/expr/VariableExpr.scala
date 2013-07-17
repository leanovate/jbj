package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.Context
import de.leanovate.jbj.ast.value.UndefinedVal

case class VariableExpr(variableName: String) extends Expr {
  def eval(ctx: Context) = ctx.findVariable(variableName).getOrElse(UndefinedVal)
}
