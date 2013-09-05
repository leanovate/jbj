package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.ast.NodePosition
import de.leanovate.jbj.core.buildin.PException

object CatchableFatalError {
  def apply(msg: String)(implicit ctx: Context) {
    if (ctx.log.catchableFatal(msg)) {

      val exception = PException.newInstance(ScalarExpr(StringVal(msg)) :: Nil)

      throw RuntimeJbjException(exception)
    }
  }
}
