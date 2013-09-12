/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PVar, PAny, PVal}
import de.leanovate.jbj.runtime.context.Context

trait Reference {
  def isConstant: Boolean

  def isDefined: Boolean

  def byVal: PVal

  def byVar: PVar

  def assign(pAny: PAny, indirect: Boolean = false)(implicit ctx: Context): PAny

  def unset()

  def +=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum + other.asVal.toNum).asVal

  def -=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum - other.asVal.toNum).asVal

  def *=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum * other.asVal.toNum).asVal

  def /=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum / other.asVal.toNum).asVal

  def !=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toStr ! other.asVal.toStr).asVal

  def ++()(implicit ctx: Context): PVal = {
    val result = byVal.copy
    assign(result.incr, indirect = true)
    result
  }

  def --(implicit ctx: Context): PVal = {
    val result = byVal.copy
    assign(result.decr, indirect = true)
    result
  }

  def dim()(implicit ctx: Context) = new DimReference(this, None)

  def dim(key: PVal)(implicit ctx: Context) = new DimReference(this, Some(key))

  def prop(name: String)(implicit ctx: Context) = new PropReference(this, name)
}

object Reference {
  def ++(ref: Reference)(implicit ctx: Context): PVal = ref.assign(ref.byVal.incr, indirect = true).asVal

  def --(ref: Reference)(implicit ctx: Context): PVal = ref.assign(ref.byVal.decr, indirect = true).asVal

}