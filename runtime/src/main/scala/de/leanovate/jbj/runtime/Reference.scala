/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.runtime.context.Context

trait Reference {
  def isDefined: Boolean

  def byVal: PVal

  def asVar: PAny

  def assign(pAny: PAny)(implicit ctx: Context): PAny

  def unset()


  def +=(other: PAny)(implicit ctx: Context): PAny = assign(this.byVal.toNum + other.asVal.toNum)

  def -=(other: PAny)(implicit ctx: Context): PAny = assign(this.byVal.toNum - other.asVal.toNum)

  def *=(other: PAny)(implicit ctx: Context): PAny = assign(this.byVal.toNum * other.asVal.toNum)

  def /=(other: PAny)(implicit ctx: Context): PAny = assign(this.byVal.toNum / other.asVal.toNum)

  def !=(other: PAny)(implicit ctx: Context): PAny = assign(this.byVal.toStr ! other.asVal.toStr)

  def ++()(implicit ctx: Context): PAny = {
    val result = byVal.copy
    assign(result.incr)
    result
  }

  def --(implicit ctx: Context): PAny = {
    val result = byVal.copy
    assign(result.decr)
    result
  }
}

object Reference {
  def ++(ref: Reference)(implicit ctx: Context): PAny = ref.assign(ref.byVal.incr)

  def --(ref: Reference)(implicit ctx: Context): PAny = ref.assign(ref.byVal.decr)

}