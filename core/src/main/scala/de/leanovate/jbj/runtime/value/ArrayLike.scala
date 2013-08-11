package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{Context, ArrayKey}
import de.leanovate.jbj.ast.NodePosition

trait ArrayLike {
  def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition): Option[ValueOrRef]

  def setAt(index: Option[ArrayKey], value: ValueOrRef)(implicit ctx: Context, position: NodePosition)

  def unsetAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition)

}
