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
  def isConstant = !value.isInstanceOf[PVar]

  def isDefined = !byVal.isNull

  def byVal = value.asVal

  def byVar = value.asVar

  def assign(pAny: PAny)(implicit ctx: Context) = {
    pAny
  }

  def unset() {
  }
}
