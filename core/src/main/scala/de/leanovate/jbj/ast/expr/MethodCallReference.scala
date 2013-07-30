package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.{Value, Context}

case class MethodCallReference(reference: Reference, methodName: String, parameters: List[Expr]) extends Reference {
  override def eval(ctx: Context) = ???

  def assignInitial(ctx: Context, value: Value) {}

  def assign(ctx: Context, value: Value) {}
}
