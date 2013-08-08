package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.Value

case class MethodCallReference(reference: Reference, methodName: String, parameters: List[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = ???

  override def assign(value: Value)(implicit ctx: Context) {}
}
