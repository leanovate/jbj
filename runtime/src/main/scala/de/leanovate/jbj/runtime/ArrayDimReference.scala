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

class ArrayDimReference(parentRef: Reference, optArrayKey: Option[PVal])(implicit ctx: Context) extends Reference {
  def isConstant = false

  override def isDefined = {
    if (optArrayKey.isEmpty)
      false
    else
      parentRef.asVal.concrete match {
        case array: ArrayLike =>
          array.getAt(optArrayKey.get).exists(!_.asVal.isNull)
        case _ =>
          false
      }
  }

  override def asVal = {
    parentRef.asVal.concrete match {
      case array: ArrayLike =>
        optArrayKey match {
          case Some(arrayKey) if arrayKey.concrete.isInstanceOf[ObjectVal] =>
            ctx.log.warn("Illegal offset type")
            NullVal
          case Some(arrayKey) =>
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
          case None =>
            throw new FatalErrorJbjException("Cannot use [] for reading")
        }
      case _ => NullVal
    }
  }

  override def asVar = {
    optParent.map {
      array =>
        optArrayKey match {
          case Some(arrayKey) if arrayKey.concrete.isInstanceOf[ObjectVal] =>
            ctx.log.warn("Illegal offset type")
            PVar()
          case Some(arrayKey) =>
            array.getAt(arrayKey) match {
              case Some(_: ObjectVal) =>
                ctx.log.warn("Illegal offset type")
                PVar()
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
    }.getOrElse {
      ctx.log.warn("Cannot use a scalar value as an array")
      PVar()
    }
  }

  override def value_=(pAny: PAny)(implicit ctx: Context) = {
    optParent.map {
      array =>
        optArrayKey match {
          case Some(arrayKey) if arrayKey.concrete.isInstanceOf[ObjectVal] =>
            ctx.log.warn("Illegal offset type")
            NullVal
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
    }.getOrElse {
      ctx.log.warn("Cannot use a scalar value as an array")
    }
    pAny
  }

  override def unset()(implicit ctx: Context) {
    if (optArrayKey.isDefined) {
      optParent.foreach {
        case array: ArrayLike =>
          array.unsetAt(optArrayKey.get)
      }
    }
  }

  private def optParent: Option[ArrayLike] = {
    if (parentRef.checkIndirect)
      parentRef.asVal.concrete match {
        case array: ArrayLike => Some(array)
        case NullVal =>
          val array = ArrayVal()
          parentRef.asVar.value = array
          Some(array)
        case _ =>
          None
      }
    else
      Some(ArrayVal())
  }
}
