package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PAny, ObjectVal}
import de.leanovate.jbj.ast.{Expr, NodePosition}

trait PMethod {
  def name: String

  def isStatic: Boolean

  def invoke(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, parameters: List[Expr]): PAny

  def invokeStatic(ctx: Context, callerPosition: NodePosition, pClass: PClass, parameters: List[Expr]): PAny

  def checkRules(pClass: PClass)(implicit ctx: Context) {}
}
