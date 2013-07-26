package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Value

abstract class ObjectVal extends Value {
  def isNull = false

  def isUndefined = false

  def unref = this

  def copy = this

  def incr = this

  def decr = this
}
