package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.runtime.context.Context

trait Reference {
  def asVal: PVal

  def asVar: PAny

  def assign(pAny: PAny): PAny

  def unset()


  def +=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum + other.asVal.toNum)

  def -=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum - other.asVal.toNum)

  def *=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum * other.asVal.toNum)

  def /=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum / other.asVal.toNum)

  def !=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toStr ! other.asVal.toStr)
}
