package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class FileNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = StringVal(position.fileName)
}
