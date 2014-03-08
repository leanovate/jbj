package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.StringVal

object Operators {
  def $(name: String)(implicit ctx: Context): Reference = new VariableReference(name)
}
