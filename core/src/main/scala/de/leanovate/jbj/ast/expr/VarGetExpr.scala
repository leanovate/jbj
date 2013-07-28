package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.UndefinedVal

case class VarGetExpr(position: FilePosition, variableName: String) extends Expr {
  def eval(ctx: Context) = ctx.findVariable(variableName).map(_.value).getOrElse(UndefinedVal)
}
