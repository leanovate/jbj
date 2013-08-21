package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.{Context, FunctionContext, MethodContext}
import de.leanovate.jbj.runtime.value.StringVal

case class FunctionNameConstExpr() extends Expr {
  def eval(implicit ctx: Context) = ctx match {
    case MethodContext(_, name, _) => StringVal(name)
    case FunctionContext(name, _) => StringVal(name.toString)
    case _ => StringVal("")
  }
}
