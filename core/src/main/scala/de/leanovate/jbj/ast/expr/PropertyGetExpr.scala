package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr, NodePosition}
import de.leanovate.jbj.runtime.Context

case class PropertyGetExpr( reference: Reference, propertyName: String) extends Expr {
  def eval(ctx: Context) = ???
}
