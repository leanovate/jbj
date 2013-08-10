package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef, Value, ObjectVal}
import de.leanovate.jbj.ast.NodePosition

trait PMethod {
  def name: String

  def isStatic: Boolean

  def invoke(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, parameters: List[ValueOrRef]): ValueOrRef

  def invokeStatic(ctx: Context, callerPosition: NodePosition, pClass: PClass, parameters: List[ValueOrRef]): ValueOrRef
}
