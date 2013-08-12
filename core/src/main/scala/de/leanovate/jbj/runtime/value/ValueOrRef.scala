package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

trait ValueOrRef {
  def toOutput(implicit ctx: Context): String

  def value: Value

  def incrRefCount()

  def decrRefCount()
}
