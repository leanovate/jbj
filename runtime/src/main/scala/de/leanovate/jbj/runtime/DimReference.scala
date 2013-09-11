package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context
import scala.Some

class DimReference(parentRef: Reference, optArrayKey: Option[PVal])(implicit ctx: Context) extends Reference {
  def isConstant = false

  def isDefined = {
    if (optArrayKey.isEmpty || !parentRef.isDefined)
      false
    else
      parentRef.byVal.concrete match {
        case array: ArrayLike =>
          array.getAt(optArrayKey.get).exists(!_.asVal.isNull)
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
            optArrayKey.get match {
              case NumericVal(idx) =>
                ctx.log.notice("Undefined offset: %d".format(idx.toLong))
              case idx =>
                ctx.log.notice("Undefined index: %s".format(idx.toStr.asString))
            }
          }
          result.map(_.asVal).getOrElse(NullVal)
        case _ => NullVal
      }
  }

  def byVar = {
    optParent.map {
      array =>
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
    }.getOrElse {
      ctx.log.warn("Cannot use a scalar value as an array")
      PVar()
    }
  }

  def assign(pAny: PAny)(implicit ctx: Context) = {
    optParent.map {
      array =>
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
    }.getOrElse {
      ctx.log.warn("Cannot use a scalar value as an array")
    }
    pAny
  }

  def unset() {
    if (optParent.isDefined && optArrayKey.isDefined) {
      optParent.get.unsetAt(optArrayKey.get)
    }
  }

  private def optParent = {
    if (!parentRef.isDefined) {
      val array = ArrayVal()
      parentRef.byVar.value = array
      Some(array)
    } else
      parentRef.byVal.concrete match {
        case array: ArrayLike => Some(array)
        case NullVal =>
          val array = ArrayVal()
          parentRef.byVar.value = array
          Some(array)
        case _ =>
          None
      }
  }
}
