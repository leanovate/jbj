package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.context.StaticContext
import de.leanovate.jbj.runtime.Context

trait StaticInitializer {
  def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context)
}
