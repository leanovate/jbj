package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.{Expr, FilePosition}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class ConstGetExpr(position: FilePosition, constName: String) extends Expr {
  def eval(ctx: Context) = {
    ctx.findConstant(constName).getOrElse {
      ctx.log.notice(position, "Use of undefined constant %s - assumed '%s'".format(constName, constName))
      StringVal(constName)
    }
  }
}
