package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{Expr, NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef, Value}

trait PFunction {
  def name: NamespaceName

  def call(parameters: List[Expr])(implicit callerCtx: Context, callerPosition: NodePosition): ValueOrRef
}
