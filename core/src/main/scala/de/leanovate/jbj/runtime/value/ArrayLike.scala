package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

trait ArrayLike {
  def size: Int

  def getAt(index: PAny)(implicit ctx: Context, position: NodePosition): Option[PAny] =
    index.value match {
      case IntegerVal(idx) => getAt(idx)
      case NumericVal(idx) => getAt(idx.toLong)
      case StringVal(idx) => getAt(idx)
      case v => getAt(v.toStr.asString)
    }

  def getAt(index: Long)(implicit ctx: Context, position: NodePosition): Option[PAny]

  def getAt(index: String)(implicit ctx: Context, position: NodePosition): Option[PAny]

  def setAt(optIndex: Option[PVal], value: PAny)(implicit ctx: Context, position: NodePosition) {
    if (optIndex.isDefined)
      setAt(optIndex.get, value)
    else
      append(value)
  }

  def setAt(index: PVal, value: PAny)(implicit ctx: Context, position: NodePosition) {
    index.value match {
      case IntegerVal(idx) => setAt(idx, value)
      case NumericVal(idx) => setAt(idx.toLong, value)
      case StringVal(idx) => setAt(idx, value)
      case v => getAt(v.toStr.asString)
    }
  }

  def setAt(index: Long, value: PAny)(implicit ctx: Context, position: NodePosition)

  def setAt(index: String, value: PAny)(implicit ctx: Context, position: NodePosition)

  def append(value: PAny)(implicit ctx: Context, position: NodePosition)

  def unsetAt(index: Long)(implicit ctx: Context, position: NodePosition)

  def unsetAt(index: String)(implicit ctx: Context, position: NodePosition)
}
