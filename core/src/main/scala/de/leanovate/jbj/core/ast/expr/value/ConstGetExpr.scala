package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.{NamespaceName, Expr}
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.runtime.context.Context

case class ConstGetExpr(constName: NamespaceName, relative: Boolean = true) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.global.findConstant(constName.toString).getOrElse {
      ctx.log.notice("Use of undefined constant %s - assumed '%s'".format(constName, constName))
      StringVal(constName.toString)
    }
  }
}
