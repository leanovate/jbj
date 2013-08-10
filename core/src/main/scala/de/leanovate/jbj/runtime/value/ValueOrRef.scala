package de.leanovate.jbj.runtime.value

trait ValueOrRef {
  def toOutput: String

  def value: Value

  def incrRefCount()

  def decrRefCount()
}
