package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.types.{PAnyParam, PException, PValParam}

object CatchableFatalError {
  def apply(msg: String)(implicit ctx: Context) {
    if (ctx.log.catchableFatal(msg)) {

      val exception = PException.newInstance(PAnyParam(StringVal(msg)) :: Nil)

      throw RuntimeJbjException(exception)
    }
  }
}
