package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{StringArrayKey, IntArrayKey, Context, ArrayKey}
import de.leanovate.jbj.ast.NodePosition

trait ArrayLike {
  def size:Int

  def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition): Option[PAny] =
    index match {
      case IntArrayKey(idx) => getAt(idx)
      case StringArrayKey(idx) => getAt(idx)
    }

  def getAt(index: Long)(implicit ctx: Context, position: NodePosition): Option[PAny]

  def getAt(index: String)(implicit ctx: Context, position: NodePosition): Option[PAny]

  def setAt(optIndex: Option[ArrayKey], value: PAny)(implicit ctx: Context, position: NodePosition) {
    optIndex match {
      case Some(IntArrayKey(idx)) => setAt(idx, value)
      case Some(StringArrayKey(idx)) => setAt(idx, value)
      case None => append(value)
    }
  }

  def setAt(index: Long, value: PAny)(implicit ctx: Context, position: NodePosition)

  def setAt(index: String, value: PAny)(implicit ctx: Context, position: NodePosition)

  def append(value: PAny)(implicit ctx: Context, position: NodePosition)

  def unsetAt(index: Long)(implicit ctx: Context, position: NodePosition)

  def unsetAt(index: String)(implicit ctx: Context, position: NodePosition)
}
