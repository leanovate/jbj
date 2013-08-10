package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{ValueOrRef, ObjectVal}
import de.leanovate.jbj.ast.{Expr, NodePosition}

trait PMethod {
  def name: String

  def isStatic: Boolean

  def invoke(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, parameters: List[Expr]): ValueOrRef

  def invokeStatic(ctx: Context, callerPosition: NodePosition, pClass: PClass, parameters: List[Expr]): ValueOrRef
}
