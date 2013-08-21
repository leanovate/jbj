package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.context.{Context, StaticContext}

trait StaticInitializer {
  def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context)
}
