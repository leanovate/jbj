package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

class PVar(private var current: Option[PVal] = None) extends PAny {
  private var prev = this
  private var next = this

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  def value = current.getOrElse(NullVal)

  def value_=(v: PVal) {
    current = Some(v)
  }

  def unset() {
    current = None
  }

  override def cleanup() {
  }

  override def asVal = value

  override def asVar = this
}

object PVar {
  def apply(): PVar = new PVar(None)

  def apply(v: PVal): PVar = new PVar(Some(v))

  def apply(optVal: Option[PAny]) = new PVar(optVal.map(_.asVal))
}
