package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

class PVar(private var current: Option[PVal] = None) extends PAny {
  private var _refCount = 0

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  def value = current.getOrElse(NullVal)

  def value_=(v: PVal) {
    current = Some(v)
  }

  def unset() {
    current = None
  }

  def refCount = _refCount

  override def asVal = value

  override def asVar = this

  override def incrRefCount() {
    _refCount += 1
  }

  override def decrRefCount() {
    _refCount -= 1
  }
}

object PVar {
  def apply(): PVar = new PVar(None)

  def apply(v: PVal): PVar = new PVar(Some(v))

  def apply(optVal: Option[PAny]) = new PVar(optVal.map(_.asVal))
}
