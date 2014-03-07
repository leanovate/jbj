package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context

trait JbjCodeUnit {
  def exec(implicit ctx: Context)
}
