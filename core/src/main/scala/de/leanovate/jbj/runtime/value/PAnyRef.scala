package de.leanovate.jbj.runtime.value

trait PAnyRef extends PAny {
  def value_=(v: PAnyVal)

  def unset()
}
