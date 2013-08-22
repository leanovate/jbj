package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

class PVar(private var current: Option[PVal] = None) extends PAny {
  private var prev = this
  private var next = this

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  protected def set(pVal: Option[PVal]) {
    current = pVal
  }

  def value = current.getOrElse(NullVal)

  def value_=(v: PVal) {
    var pVar = this
    do {
      pVar.set(Some(v))
      pVar = pVar.next
    } while (pVar != this)
  }

  def ref = this

  def ref_=(pVar: PVar) {
    if (pVar != this) {
      unlink()
      set(pVar.current)
      prev = pVar.prev
      next = pVar
      prev.next = this
      pVar.prev = this
    }
  }

  def unset() {
    unlink()
    current = None
  }

  override def cleanup() {
    unlink()
  }

  override def asVal = value

  override def asVar = this

  private def unlink() {
    next.prev = prev
    prev.next = next
    prev = this
    next = this
  }

  override def toString: String = {
    val builder = new StringBuilder("PVar(")
    builder.append(value)
    var other = this
    do {
      builder.append(", ")
      builder.append(other.hashCode())
      other = other.next
    } while (other != this)
    builder.append(")")
    builder.result()
  }

  def +=(other: PAny)(implicit ctx: Context): PAny = {
    value_=(this.asVal.toNum + other.asVal.toNum)
    value
  }

  def -=(other: PAny)(implicit ctx: Context): PAny = {
    value_=(this.asVal.toNum - other.asVal.toNum)
    value
  }

  def *=(other: PAny)(implicit ctx: Context): PAny = {
    value_=(this.asVal.toNum * other.asVal.toNum)
    value
  }

  def /=(other: PAny)(implicit ctx: Context): PAny = {
    value_=(this.asVal.toNum / other.asVal.toNum)
    value
  }

}

object PVar {
  def apply(): PVar = new PVar(None)

  def apply(v: PVal): PVar = new PVar(Some(v))

  def apply(optVal: Option[PAny]) = new PVar(optVal.map(_.asVal))
}
