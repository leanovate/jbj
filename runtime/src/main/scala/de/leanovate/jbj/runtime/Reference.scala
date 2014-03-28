/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.{PIterator, PIteratorAggregate, PArrayAccess}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.Some

trait Reference extends PAny {
  def isConstant: Boolean

  def isDefined: Boolean

  def :=(v: PAny)(implicit ctx: Context): PAny

  def unset()(implicit ctx: Context)

  def checkIndirect: Boolean = true

  def +=(other: PAny)(implicit ctx: Context): PVal = :=(this.asVal.toNum + other.asVal.toNum).asVal

  def -=(other: PAny)(implicit ctx: Context): PVal = :=(this.asVal.toNum - other.asVal.toNum).asVal

  def *=(other: PAny)(implicit ctx: Context): PVal = :=(this.asVal.toNum * other.asVal.toNum).asVal

  def /=(other: PAny)(implicit ctx: Context): PVal = :=(this.asVal.toNum / other.asVal.toNum).asVal

  def !!=(other: PAny)(implicit ctx: Context): PVal = :=(this.asVal.toStr !! other.asVal.toStr).asVal

  def ++()(implicit ctx: Context): PVal = {
    val result = asVal.copy
    if (checkIndirect)
      :=(result.incr)
    result
  }

  def --(implicit ctx: Context): PVal = {
    val result = asVal.copy
    if (checkIndirect)
      :=(result.decr)
    result
  }

  def dim(key: PAny)(implicit ctx: Context): Reference = dim(Some(key.asVal))

  def dim(optKey: Option[PVal] = None)(implicit ctx: Context): Reference = {
    if (isDefined) {
      asVal.concrete match {
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
    if (asVar.refCount > 1) {
      foreachByVar(f)
    } else {
      asVal.concrete.foreachByVal(f)
    }
  }

  def foreachByVar[R](f: (PVal, PVar) => Option[R])(implicit ctx: Context): Option[R] = {
    var currentIt = asVal.concrete match {
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
      ctx.currentPosition = position
      asVal.concrete match {
        case array: ArrayVal =>
          currentIt = array.updateIteratorState(currentIt.copy(fixedEntries = false))
        case obj: ObjectVal =>
          currentIt = obj.updateIteratorState(currentIt.copy(fixedEntries = false))
        case _ =>
          ctx.log.warn("Invalid argument supplied for foreach()")
          return Option.empty[R]
      }

      if (currentIt.hasNext) {
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
        asVal.concrete match {
          case array: ArrayVal =>
            result = f(key, value)
          case obj: ObjectVal =>
            result = f(key, value)
          case _ =>
            ctx.log.warn("Invalid argument supplied for foreach()")
            return Option.empty[R]
        }
      }
    }
    result
  }
}
