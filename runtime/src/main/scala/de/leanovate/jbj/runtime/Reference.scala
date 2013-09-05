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

  def asVal: PVal

  def asVar: PAny

  def assign(pAny: PAny)(implicit ctx: Context): PAny

  def unset()


  def +=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum + other.asVal.toNum)

  def -=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum - other.asVal.toNum)

  def *=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum * other.asVal.toNum)

  def /=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toNum / other.asVal.toNum)

  def !=(other: PAny)(implicit ctx: Context): PAny = assign(this.asVal.toStr ! other.asVal.toStr)

  def ++()(implicit ctx: Context): PAny = {
    val result = asVal
    assign(result.incr)
    result
  }

  def --(implicit ctx: Context): PAny = {
    val result = asVal
    assign(result.decr)
    result
  }
}

object Reference {
  def ++(ref: Reference)(implicit ctx: Context): PAny = ref.assign(ref.asVal.incr)

  def --(ref: Reference)(implicit ctx: Context): PAny = ref.assign(ref.asVal.decr)

}