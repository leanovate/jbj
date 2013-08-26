package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.{Context, MethodContext, InstanceContext}
import de.leanovate.jbj.core.runtime.value.StringVal

case class ClassNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = ctx match {
    case InstanceContext(inst, _) => StringVal(inst.pClass.name.toString)
    case MethodContext(_, pMethod, _) => StringVal(pMethod.declaringClass.name.toString)
    case _ => StringVal("")
  }
}
