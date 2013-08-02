package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

class FatalErrorJbjException(ctx: Context, position: NodePosition, message: String) extends JbjException(message) {
  ctx.log.fatal(position, message)
}
