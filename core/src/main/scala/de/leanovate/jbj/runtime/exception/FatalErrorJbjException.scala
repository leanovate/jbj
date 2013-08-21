package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.context.Context

class FatalErrorJbjException(message: String)(implicit ctx: Context) extends JbjException(message) {
  ctx.log.fatal(message)
}
