package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.Context

case class CallExpr(position: FilePosition, functionName: String, parameters: List[Expr]) extends Expr {
  def eval(ctx: Context) = ctx.findFunction(functionName).map {
    func => func.call(ctx.global, position, parameters.map(_.eval(ctx)))
  }.getOrElse(throw new IllegalArgumentException("No such function: " + functionName))
}
