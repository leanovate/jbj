package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.{Context, MethodContext, InstanceContext}
import de.leanovate.jbj.runtime.value.StringVal

case class ClassNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = ctx match {
    case InstanceContext(inst, _) => StringVal(inst.pClass.name.toString)
    case MethodContext(_, pClass, _, _) => StringVal(pClass.name.toString)
    case _ => StringVal("")
  }
}
