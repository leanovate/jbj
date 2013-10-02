/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, StringVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object ByteArrayConverter extends Converter[Array[Byte], StringVal] {
  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVal.toStr.chars

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byVal.toStr.chars

  override def toScala(value: StringVal)(implicit ctx: Context) = value.chars

  override def toJbj(value: Array[Byte])(implicit ctx: Context) = StringVal(value)
}
