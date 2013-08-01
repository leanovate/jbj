package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.ObjectVal

trait PClass {
  def name: NamespaceName

  def newInstance(ctx: Context, callerPosition: NodePosition, parameters: List[Value]): Value

  def invokeMethod(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, methodName:String, parameters: List[Value]): Either[Value, ValueRef]
}