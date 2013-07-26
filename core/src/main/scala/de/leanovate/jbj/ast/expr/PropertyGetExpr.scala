package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, FilePosition}
import de.leanovate.jbj.runtime.Context

case class PropertyGetExpr(position: FilePosition, variableName: String, propertyName: String) extends Expr {
  def eval(ctx: Context) = ???
}
