package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

class PVar(private var current: Option[PVal] = None) extends PAny {
  private var prev = this
  private var next = this

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  def value = current.getOrElse(NullVal)

  def value_=(v: PVal) {
    var pVar = this
    do {
      pVar.current = Some(v)
      pVar = pVar.next
    } while (pVar != this)
  }

  def ref = this

  def ref_=(pVar: PVar) {
    if ( pVar != this) {
      value_=(pVar.asVal)
      prev = pVar.prev
      next = pVar
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
    val oldNext = next
    val oldPrev = prev

    oldNext.prev = oldPrev
    oldPrev.next = oldNext
    prev = this
    next = this
  }
}

object PVar {
  def apply(): PVar = new PVar(None)

  def apply(v: PVal): PVar = new PVar(Some(v))

  def apply(optVal: Option[PAny]) = new PVar(optVal.map(_.asVal))
}
