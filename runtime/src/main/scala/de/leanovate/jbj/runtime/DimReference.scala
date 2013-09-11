/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context
import scala.Some
import de.leanovate.jbj.runtime.types.PArrayAccess

class DimReference(parentRef: Reference, optArrayKey: Option[PVal])(implicit ctx: Context) extends Reference {
  def isConstant = false

  def isDefined = {
    if (optArrayKey.isEmpty || !parentRef.isDefined)
      false
    else
      parentRef.byVal.concrete match {
        case array: ArrayLike =>
          array.getAt(optArrayKey.get).exists(!_.asVal.isNull)
        case obj: ObjectVal if obj.instanceOf(PArrayAccess) =>
          PArrayAccess.cast(obj).offsetExists(optArrayKey.get)
        case _ =>
          false
      }
  }

  def byVal = {
    if (optArrayKey.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")
    else
      parentRef.byVal.concrete match {
        case array: ArrayLike =>
          val result = array.getAt(optArrayKey.get)
          if (!result.isDefined) {
            optArrayKey.get.concrete match {
              case IntegerVal(idx) =>
                ctx.log.notice("Undefined offset: %d".format(idx))
              case DoubleVal(idx) =>
                ctx.log.notice("Undefined offset: %d".format(idx.toLong))
              case str: StringVal if str.isStrongNumericPattern =>
                ctx.log.notice("Undefined offset: %d".format(str.toInteger.asLong))
              case idx =>
                ctx.log.notice("Undefined index: %s".format(idx.toStr.asString))
            }
          }
          result.map(_.asVal).getOrElse(NullVal)
        case obj: ObjectVal if obj.instanceOf(PArrayAccess) =>
          PArrayAccess.cast(obj).offsetGet(optArrayKey.get).asVal
        case _ => NullVal
      }
  }

  def byVar = {
    optParent.map {
      case array: ArrayLike =>
        optArrayKey match {
          case Some(arrayKey) =>
            array.getAt(arrayKey) match {
              case Some(valueRef: PVar) =>
                valueRef
              case someValue =>
                val result = PVar(someValue)
                array.setAt(arrayKey, result)
                result
            }
          case None =>
            val result = PVar()
            array.setAt(None, result)
            result
        }
      case obj: ObjectVal =>
        PVar()
    }.getOrElse {
      ctx.log.warn("Cannot use a scalar value as an array")
      PVar()
    }
  }

  def assign(pAny: PAny)(implicit ctx: Context) = {
    optParent.map {
      case array: ArrayLike =>
        optArrayKey match {
          case Some(arrayKey) =>
            array.getAt(arrayKey) match {
              case Some(pVar: PVar) if pAny.isInstanceOf[PVal] =>
                pVar.value = pAny.asInstanceOf[PVal]
              case _ =>
                array.setAt(optArrayKey, pAny)
            }
          case None =>
            array.setAt(optArrayKey, pAny)
        }
      case obj: ObjectVal =>
        optArrayKey match {
          case Some(arrayKey) =>
            PArrayAccess.cast(obj).offsetSet(arrayKey, pAny.asVal)
          case None =>
            PArrayAccess.cast(obj).offsetSet(NullVal, pAny.asVal)
        }
    }.getOrElse {
      ctx.log.warn("Cannot use a scalar value as an array")
    }
    pAny
  }

  def unset() {
    if (optArrayKey.isDefined) {
      optParent.foreach {
        case array: ArrayLike =>
          array.unsetAt(optArrayKey.get)
        case obj: ObjectVal =>
          PArrayAccess.cast(obj).offsetUnset(optArrayKey.get)
      }
    }
  }

  private def optParent: Option[PVal] = {
    if (!parentRef.isDefined) {
      val array = ArrayVal()
      parentRef.byVar.value = array
      Some(array)
    } else
      parentRef.byVal.concrete match {
        case array: ArrayLike => Some(array)
        case obj: ObjectVal if obj.instanceOf(PArrayAccess) =>
          Some(obj)
        case NullVal =>
          val array = ArrayVal()
          parentRef.byVar.value = array
          Some(array)
        case _ =>
          None
      }
  }
}
