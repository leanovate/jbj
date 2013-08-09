package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}

case class MethodCallReference(reference: Reference, methodName: String, parameters: List[Expr]) extends Reference {
  override def evalRef(implicit ctx: Context) = ???

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {}
}
