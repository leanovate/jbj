package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, FilePosition}
import de.leanovate.jbj.runtime.Context

case class CallMethodExpr(position: FilePosition, variableName: String, methodName: String, parameters: List[Expr])
  extends Expr {
  def eval(ctx: Context) = ???
}
