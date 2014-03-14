/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.exception.WarnWithResultJbjException

trait PFunction {
  def name: NamespaceName

  def parameters: Seq[PParamDef]

  def call(parameters: Seq[PParam])(implicit callerCtx: Context): PAny = {
    try {
      doCall(parameters)
    } catch {
      case e: WarnWithResultJbjException =>
        callerCtx.log.warn(e.getMessage)
        e.result
    }
  }

  def doCall(parameters: Seq[PParam])(implicit callerCtx: Context): PAny
}
