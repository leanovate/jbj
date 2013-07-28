package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr, FilePosition}
import de.leanovate.jbj.runtime.Context

case class CallMethodExpr(position: FilePosition, reference: Reference, methodName: String, parameters: List[Expr])
  extends Expr {
  def eval(ctx: Context) = ???
}
