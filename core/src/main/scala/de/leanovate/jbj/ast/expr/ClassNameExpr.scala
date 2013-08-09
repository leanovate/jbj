package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class ClassNameExpr(className: Name) extends Expr {
  def eval(implicit ctx: Context) = StringVal(className.evalName.toString)
}
