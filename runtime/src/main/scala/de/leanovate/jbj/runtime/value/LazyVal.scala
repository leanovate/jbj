/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

abstract class LazyVal extends PVal {
  def value: PConcreteVal

  override def toOutput(implicit ctx: Context) = value.toOutput

  override def toStr(implicit ctx: Context) = value.toStr

  override def toNum(implicit ctx: Context) = value.toNum

  override def toInteger(implicit ctx: Context) = value.toInteger

  override def toDouble = value.toDouble

  override def toBool = value.toBool

  override def toArray(implicit ctx: Context) = value.toArray

  override def isScalar = value.isScalar

  override def isNull = value.isNull

  override def copy = value.copy

  override def incr(implicit ctx: Context) = value.incr

  override def decr(implicit ctx: Context) = value.decr

  override def typeName(simple: Boolean = false) = value.typeName(simple)

  override def compare(other: PVal)(implicit ctx: Context) = value.compare(other)

  override def asVal = value

  override def concrete = value

  override def isCallable(implicit ctx: Context) = value.isCallable

  override def call(params: List[PParam])(implicit ctx: Context) = value.call(params)
}
