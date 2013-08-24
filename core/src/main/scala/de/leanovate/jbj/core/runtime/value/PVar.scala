package de.leanovate.jbj.core.runtime.value

import de.leanovate.jbj.core.runtime.context.Context

class PVar(private var current: Option[PVal] = None) extends PAny {
  private var _refCount = 0

  def refCount = _refCount

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  protected def set(pVal: Option[PVal]) {
    current = pVal
  }

  def value = current.getOrElse(NullVal)

  def value_=(v: PVal) {
    current = Some(v)
  }

  def unset() {
    current = None
  }

  override def retain() {
    _refCount += 1
  }

  override def release() {
    _refCount -= 1
  }

  override def asVal = value

  override def asVar = this

  override def toString: String = {
    val builder = new StringBuilder("PVar(")
    builder.append(value)
    builder.append(")")
    builder.result()
  }
}

object PVar {
  def apply(): PVar = new PVar(None)

  def apply(v: PVal): PVar = new PVar(Some(v))

  def apply(optVal: Option[PAny]) = new PVar(optVal.map(_.asVal))
}
