/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

abstract class LazyVal extends PVal {
  def value: PConcreteVal

  def toOutput(implicit ctx: Context) = value.toOutput

  def toStr(implicit ctx: Context) = value.toStr

  def toNum = value.toNum

  def toInteger = value.toInteger

  def toDouble = value.toDouble

  def toBool = value.toBool

  def toArray(implicit ctx: Context) = value.toArray

  def isNull = value.isNull

  def copy = value.copy

  def incr = value.incr

  def decr = value.decr

  def typeName(simple :Boolean = false) = value.typeName(simple)

  def compare(other: PVal)(implicit ctx: Context) = value.compare(other)

  override def asVal = value

  override def concrete = value
}
