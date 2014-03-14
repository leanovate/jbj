/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PVar, PAny}
import de.leanovate.jbj.runtime.context.Context

class ConstantReference(value: PAny) extends Reference {
  override def isConstant = !value.isInstanceOf[PVar]

  override def isDefined = !asVal.isNull

  override def asVal = value.asVal

  override def asVar = value.asVar

  override def value_=(pAny: PAny)(implicit ctx: Context) = {
    pAny
  }

  override def unset()(implicit ctx: Context) {
  }
}
