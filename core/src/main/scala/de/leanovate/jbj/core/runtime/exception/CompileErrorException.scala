package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.api.JbjException

class CompileErrorException(message: String)(implicit ctx: Context) extends JbjException(message) {

  ctx.log.compileError(message)
}