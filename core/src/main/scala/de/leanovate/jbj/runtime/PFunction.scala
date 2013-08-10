package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef, Value}

trait PFunction {
  def name: NamespaceName

  def call(ctx: Context, callerPosition: NodePosition, parameters: List[ValueOrRef]): ValueOrRef
}
