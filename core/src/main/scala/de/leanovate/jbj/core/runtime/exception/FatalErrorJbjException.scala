package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.runtime.context.Context

class FatalErrorJbjException(message: String)(implicit ctx: Context) extends JbjException(message) {
  ctx.log.fatal(message)
}
