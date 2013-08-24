package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.runtime.context.{Context, MethodContext}
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.ast.Expr

case class MethodNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = ctx match {
    case MethodContext(inst, pClass, name,  _) => StringVal(pClass.name.toString + "::" + name)
    case _ => StringVal("")
  }

}
