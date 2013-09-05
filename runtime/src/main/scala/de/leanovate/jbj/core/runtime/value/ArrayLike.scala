/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.value

import de.leanovate.jbj.core.runtime.context.Context

trait ArrayLike {
  def size: Int

  def getAt(index: PAny)(implicit ctx: Context): Option[PAny] =
    index.asVal match {
      case IntegerVal(idx) => getAt(idx)
      case NumericVal(idx) => getAt(idx.toLong)
      case StringVal(idx) => getAt(idx)
      case v => getAt(v.toStr.asString)
    }

  def getAt(index: Long)(implicit ctx: Context): Option[PAny]

  def getAt(index: String)(implicit ctx: Context): Option[PAny]

  def setAt(optIndex: Option[PVal], value: PAny)(implicit ctx: Context) {
    if (optIndex.isDefined)
      setAt(optIndex.get, value)
    else
      append(value)
  }

  def setAt(index: PVal, value: PAny)(implicit ctx: Context) {
    index.asVal match {
      case IntegerVal(idx) => setAt(idx, value)
      case NumericVal(idx) => setAt(idx.toLong, value)
      case StringVal(idx) => setAt(idx, value)
      case v => setAt(v.toStr.asString, value)
    }
  }

  def setAt(index: Long, value: PAny)(implicit ctx: Context)

  def setAt(index: String, value: PAny)(implicit ctx: Context)

  def append(value: PAny)(implicit ctx: Context)

  def unsetAt(index: PVal)(implicit ctx: Context) {
    index.asVal match {
      case IntegerVal(idx) => unsetAt(idx)
      case NumericVal(idx) => unsetAt(idx.toLong)
      case StringVal(idx) => unsetAt(idx)
      case v => unsetAt(v.toStr.asString)
    }
  }

  def unsetAt(index: Long)(implicit ctx: Context)

  def unsetAt(index: String)(implicit ctx: Context)
}
