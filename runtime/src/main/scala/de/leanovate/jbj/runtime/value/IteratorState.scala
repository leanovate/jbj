/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import scala.collection.mutable

trait IteratorState {
  protected def currentKeyValue(implicit ctx: Context): (PVal, PAny)

  def currentKey(implicit ctx: Context): PVal = currentKeyValue._1

  def currentValue(implicit ctx: Context): PAny = currentKeyValue._2

  def currentValue_=(pAny: PAny)(implicit ctx: Context)

  def hasNext: Boolean

  def advance()

  def copy(fixedEntries: Boolean): IteratorState

  def current(implicit ctx: Context): PVal =
    if (hasNext)
      keyValueArray(currentKey, currentValue)
    else
      BooleanVal.FALSE

  def next()(implicit ctx: Context): PVal =
    if (hasNext) {
      val result = keyValueArray(currentKey, currentValue)
      advance()
      result
    } else
      BooleanVal.FALSE

  def isCompatible(map: mutable.LinkedHashMap[_, _]): Boolean

  private def keyValueArray(key: PVal, value: PAny)(implicit ctx: Context): PVal = {
    ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
      Some(IntegerVal(0)) -> key, Some(StringVal("key")) -> key)
  }
}
