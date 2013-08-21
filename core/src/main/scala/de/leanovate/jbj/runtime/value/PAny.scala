package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

trait PAny {
  def toOutput(implicit ctx: Context): String

  def asVal: PVal

  def asVar: PVar

  def cleanup() {
  }

  def +(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum + other.asVal.toNum

  def -(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum - other.asVal.toNum

  def *(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum * other.asVal.toNum

  def /(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum / other.asVal.toNum

  def %(other: PAny)(implicit ctx: Context): PAny = this.asVal.toInteger % other.asVal.toInteger

  def dot(other: PAny)(implicit ctx: Context): PAny = this.asVal.toStr dot other.asVal.toStr
}
