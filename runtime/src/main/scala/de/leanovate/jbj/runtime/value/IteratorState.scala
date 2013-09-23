/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

trait IteratorState {
  protected def currentKeyValue: (Any, PAny)

  def hasNext: Boolean

  def advance()

  def current(implicit ctx: Context): PVal =
    if (hasNext)
      keyValueArray(currentKeyValue)
    else
      BooleanVal.FALSE

  def next()(implicit ctx: Context): PVal =
    if (hasNext) {
      val result = keyValueArray(currentKeyValue)
      advance()
      result
    } else
      BooleanVal.FALSE

  private def keyValueArray(keyValue: (Any, PAny))(implicit ctx: Context): PVal = keyValue match {
    case (key: Long, value) =>
      ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
        Some(IntegerVal(0)) -> IntegerVal(key), Some(StringVal("key")) -> IntegerVal(key))
    case (key: String, value) =>
      ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
        Some(IntegerVal(0)) -> StringVal(key), Some(StringVal("key")) -> StringVal(key))
  }
}
