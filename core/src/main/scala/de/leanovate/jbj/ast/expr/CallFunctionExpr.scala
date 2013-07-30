package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context

case class CallFunctionExpr(functionName: String, arguments: List[Expr]) extends Expr {
  def eval(ctx: Context) = ctx.findFunction(functionName).map {
    func => func.call(ctx.global, position, arguments.map(_.eval(ctx)))
  }.getOrElse(throw new IllegalArgumentException("No such function: " + functionName))
}
