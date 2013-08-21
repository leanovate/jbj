package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Expr}
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.context.Context

case class ClassNameExpr(className: Name) extends Expr {
  def eval(implicit ctx: Context) = StringVal(className.evalName.toString)
}
