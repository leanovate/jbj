package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.buildin.Exception
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.ast.NodePosition

object CatchableFatalError {
  def apply(msg: String, callerPosition: NodePosition, definitionPosition: Option[NodePosition])(implicit ctx: Context) {
    if (ctx.log.catchableFatal(msg, callerPosition, definitionPosition)) {

      val exception = Exception.newInstance(ScalarExpr(StringVal(msg)) :: Nil)

      throw RuntimeJbjException(exception)
    }
  }
}
