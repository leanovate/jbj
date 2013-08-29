package de.leanovate.jbj.core.runtime.value

import de.leanovate.jbj.core.runtime.context.Context

trait ConstVal {
  def asVal(implicit ctx: Context): PVal
}
