/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import scala.Some
import de.leanovate.jbj.runtime.types.PArrayAccess

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

  def dim()(implicit ctx: Context): Reference = {
    if (isDefined) {
      byVal.concrete match {
        case obj: ObjectVal if obj.instanceOf(PArrayAccess) =>
          new ObjectDimReference(PArrayAccess.cast(obj), None)
        case _ =>
          new ArrayDimReference(this, None)
      }
    } else
      new UndefDimReference(this, None)
  }

  def dim(key: PVal)(implicit ctx: Context): Reference = {
    if (isDefined) {
      byVal.concrete match {
        case obj: ObjectVal if obj.instanceOf(PArrayAccess) =>
          new ObjectDimReference(PArrayAccess.cast(obj), Some(key))
        case _ =>
          new ArrayDimReference(this, Some(key))
      }
    } else
      new UndefDimReference(this, Some(key))
  }

  def prop(name: String)(implicit ctx: Context) = new PropReference(this, name)
}

object Reference {
  def ++(ref: Reference)(implicit ctx: Context): PVal = ref.assign(ref.byVal.incr, indirect = true).asVal

  def --(ref: Reference)(implicit ctx: Context): PVal = ref.assign(ref.byVal.decr, indirect = true).asVal

}