package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference, Name}
import de.leanovate.jbj.runtime.value.Value
import de.leanovate.jbj.runtime.Context

case class CallStaticMethodReference(className: Name, methodName: Name, parameters: List[Expr]) extends Reference {
  def eval(implicit ctx: Context) = ???

  def assign(value: Value)(implicit ctx: Context) {}
}
