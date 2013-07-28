package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr, FilePosition}
import de.leanovate.jbj.runtime.Context

case class PropertyGetExpr(position: FilePosition, reference: Reference, propertyName: String) extends Expr {
  def eval(ctx: Context) = ???
}
