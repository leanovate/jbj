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

  def assign(pAny: PAny)(implicit ctx: Context): PAny

  def unset()

  def checkIndirect: Boolean = true

  def +=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum + other.asVal.toNum).asVal

  def -=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum - other.asVal.toNum).asVal

  def *=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum * other.asVal.toNum).asVal

  def /=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toNum / other.asVal.toNum).asVal

  def !=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toStr ! other.asVal.toStr).asVal

  def ++()(implicit ctx: Context): PVal = {
    val result = byVal.copy
    if (checkIndirect)
      assign(result.incr)
    result
  }

  def --(implicit ctx: Context): PVal = {
    val result = byVal.copy
    if (checkIndirect)
      assign(result.decr)
    result
  }

  def dim(key: PVal)(implicit ctx: Context): Reference = dim(Some(key))

  def dim(optKey: Option[PVal] = None)(implicit ctx: Context): Reference = {
    if (isDefined) {
      byVal.concrete match {
        case obj: ObjectVal if obj.instanceOf(PArrayAccess) =>
          new ObjectDimReference(PArrayAccess.cast(obj), optKey)
        case str:StringVal =>
          new StringDimReference(str, optKey)
        case _ =>
          new ArrayDimReference(this, optKey)
      }
    } else
      new UndefDimReference(this, optKey)
  }

  def prop(name: String)(implicit ctx: Context) = new PropReference(this, name)
}

object Reference {
  def ++(ref: Reference)(implicit ctx: Context): PVal =
    if (ref.checkIndirect) ref.assign(ref.byVal.incr).asVal else ref.byVal.incr

  def --(ref: Reference)(implicit ctx: Context): PVal =
    if (ref.checkIndirect) ref.assign(ref.byVal.decr).asVal else ref.byVal.decr

}