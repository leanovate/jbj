package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.{Name, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{BooleanVal, ObjectVal}

case class InstanceOfExpr(expr: Expr, className: Name) extends Expr {
  def eval(implicit ctx: Context) = expr.eval match {
    case obj: ObjectVal =>
      ctx.global.findClass(className.evalNamespaceName).map {
        pClass => BooleanVal(obj.instanceOf(pClass))
      }.getOrElse(BooleanVal.FALSE)
    case _ => BooleanVal.FALSE
  }
}
