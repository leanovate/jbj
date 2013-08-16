package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

trait PAny {
  def toOutput(implicit ctx: Context): String

  def value: PAnyVal

  def incrRefCount()

  def decrRefCount()
}
