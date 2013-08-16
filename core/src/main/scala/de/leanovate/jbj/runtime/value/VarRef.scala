package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

class VarRef(private var current: Option[PAnyVal] = None) extends PAnyRef {
  private var _refCount = 0

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  def value = current.getOrElse(NullVal)

  def value_=(v: PAnyVal) {
    current = Some(v)
  }

  def unset() {
    current = None
  }

  def refCount = _refCount

  override def incrRefCount() {
    _refCount += 1
  }

  override def decrRefCount() {
    _refCount -= 1
  }
}

object VarRef {
  def apply(): VarRef = new VarRef(None)

  def apply(v: PAnyVal): VarRef = new VarRef(Some(v))

  def apply(optVal: Option[PAny]) = new VarRef(optVal.map(_.value))
}
