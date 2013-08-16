package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{Expr, NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.{PAny, VarRef, PAnyVal}

trait PFunction {
  def name: NamespaceName

  def call(parameters: List[Expr])(implicit callerCtx: Context, callerPosition: NodePosition): PAny
}
