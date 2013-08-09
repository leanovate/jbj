package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{Value, ObjectVal}
import de.leanovate.jbj.ast.NodePosition

trait PMethod {
  def name: String

  def isStatic: Boolean

  def call(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, parameters: List[Value]): Either[Value, ValueRef]
}
