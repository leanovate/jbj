package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.{NamespaceName, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class ConstGetExpr(constName: NamespaceName, relative: Boolean = true) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.findConstant(constName.toString).getOrElse {
      ctx.log.notice("Use of undefined constant %s - assumed '%s'".format(constName, constName))
      StringVal(constName.toString)
    }
  }
}
