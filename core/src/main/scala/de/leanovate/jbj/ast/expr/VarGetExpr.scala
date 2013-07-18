package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.UndefinedVal

case class VarGetExpr(variableName: String) extends Expr {
  def eval(ctx: Context) = ctx.findVariable(variableName).getOrElse(UndefinedVal)
}
