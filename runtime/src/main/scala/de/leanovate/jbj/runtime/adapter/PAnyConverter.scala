/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PParam

object PAnyConverter extends Converter[PAny, PAny] {
  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byVal

  override def toScala(value: PAny)(implicit ctx: Context) = value

  override def toJbj(valueOrRef: PAny)(implicit ctx: Context) = valueOrRef
}
