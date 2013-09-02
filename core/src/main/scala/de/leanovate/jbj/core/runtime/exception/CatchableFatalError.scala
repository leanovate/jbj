package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.buildin.Exception
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr

object CatchableFatalError {
  def apply(msg: String)(implicit ctx: Context): RuntimeJbjException = {
    ctx.log.catchableFatal(msg)

    val exception = Exception.newInstance(ScalarExpr(StringVal(msg)) :: Nil)

    RuntimeJbjException(exception)
  }
}
