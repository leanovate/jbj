package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context._
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.runtime.context.MethodContext
import de.leanovate.jbj.core.runtime.context.ClassContext
import de.leanovate.jbj.core.runtime.context.InstanceContext

case class ClassNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = ctx match {
    case InstanceContext(_, pClass, _) => StringVal(pClass.name.toString)
    case ClassContext(pClass, _, _) => StringVal(pClass.name.toString)
    case MethodContext(_, pMethod, _) => StringVal(pMethod.declaringClass.name.toString)
    case StaticMethodContext(pMethod, _) => StringVal(pMethod.declaringClass.name.toString)
    case _ => StringVal("")
  }
}
