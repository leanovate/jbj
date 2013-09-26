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
import de.leanovate.jbj.runtime.types.{PIterator, PIteratorAggregate, PArrayAccess}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

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

  def __=(other: PAny)(implicit ctx: Context): PVal = assign(this.byVal.toStr __ other.asVal.toStr).asVal

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
        case str: StringVal =>
          new StringDimReference(str, optKey)
        case _ =>
          new ArrayDimReference(this, optKey)
      }
    } else
      new UndefDimReference(this, optKey)
  }

  def prop(name: String)(implicit ctx: Context) = new PropReference(this, name)

  def foreachByVal[R](f: (PVal, PAny) => Option[R])(implicit ctx: Context) = {
    if (byVar.refCount > 1) {
      foreachByVar(f)
    } else {
      byVal.concrete.foreachByVal(f)
    }
  }

  def foreachByVar[R](f: (PVal, PVar) => Option[R])(implicit ctx: Context): Option[R] = {
    var currentIt = byVal.concrete match {
      case array: ArrayVal =>
        array.iteratorReset()
        array.iteratorReset().copy(fixedEntries = false)
      case obj: ObjectVal =>
        if (obj.instanceOf(PIteratorAggregate))
          throw new FatalErrorJbjException("An iterator cannot be used with foreach by reference")
        else if (obj.instanceOf(PIterator))
          throw new FatalErrorJbjException("An iterator cannot be used with foreach by reference")
        else {
          obj.iteratorReset().copy(fixedEntries = false)
        }
      case _ =>
        ctx.log.warn("Invalid argument supplied for foreach()")
        return None
    }

    val position = ctx.currentPosition
    var result = Option.empty[R]
    while (currentIt.hasNext && result.isEmpty) {
      val key = currentIt.currentKey
      val value = currentIt.currentValue match {
        case pVar: PVar =>
          pVar
        case pVal: PVal =>
          val pVar = PVar(pVal)
          currentIt.currentValue = pVar
          pVar
      }
      currentIt.advance()
      ctx.currentPosition = position
      byVal.concrete match {
        case array: ArrayVal =>
          result = f(key, value)
          currentIt = array.updateIteratorState(currentIt.copy(fixedEntries = false))
        case obj: ObjectVal =>
          result = f(key, value)
          currentIt = obj.updateIteratorState(currentIt.copy(fixedEntries = false))
        case _ =>
          ctx.log.warn("Invalid argument supplied for foreach()")
          return Option.empty[R]
      }
    }
    result
  }
}

object Reference {
  def ++(ref: Reference)(implicit ctx: Context): PVal =
    if (ref.checkIndirect) ref.assign(ref.byVal.incr).asVal else ref.byVal.incr

  def --(ref: Reference)(implicit ctx: Context): PVal =
    if (ref.checkIndirect) ref.assign(ref.byVal.decr).asVal else ref.byVal.decr

}