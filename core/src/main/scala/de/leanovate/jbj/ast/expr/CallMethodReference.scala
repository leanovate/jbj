package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference, Expr}
import de.leanovate.jbj.runtime.{Value, Context}

case class CallMethodReference(instanceExpr: Expr, methodName: Name, parameters: List[Expr]) extends Reference {
  override def eval(ctx: Context) = ???

  override def assign(ctx: Context, value: Value) {}
}
