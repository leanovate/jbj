package de.leanovate.jbj.runtime.buildin

import scala.collection.Map
import de.leanovate.jbj.runtime.value.PAny
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.ast.NamespaceName
import de.leanovate.jbj.ast.ClassEntry
import de.leanovate.jbj.runtime.PClass

object StdClass extends PClass {
  override def classEntry = ClassEntry.CLASS

  override def name = NamespaceName(relative = false, "stdClass")

  override def superClass = None

  override def newInstance(parameters: List[Expr])(implicit ctx: Context, callerPosition: NodePosition) =
    new ObjectVal(this, instanceCounter.incrementAndGet(), mutable.LinkedHashMap.empty[Any, PAny])

  override def methods = Map.empty
}
