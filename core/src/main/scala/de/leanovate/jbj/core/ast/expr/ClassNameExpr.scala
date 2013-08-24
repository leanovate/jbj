package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.runtime.context.Context

case class ClassNameExpr(className: Name) extends Expr {
  def eval(implicit ctx: Context) = StringVal(className.evalName.toString)
}
