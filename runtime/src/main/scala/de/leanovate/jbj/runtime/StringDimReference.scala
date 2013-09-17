/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.IntegerVal

class StringDimReference(parentStr: StringVal, optArrayKey: Option[PVal])(implicit ctx: Context) extends Reference {
  def isConstant = false

  override def isDefined = {
    if (optArrayKey.isEmpty)
      false
    else
      parentStr.getAt(optArrayKey.get).exists(!_.asVal.isNull)
  }

  override def byVal = {
    if (optArrayKey.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")
    else {
      val result = parentStr.getAt(optArrayKey.get)
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
    }
  }

  override def byVar = {
    optArrayKey match {
      case Some(arrayKey) =>
        parentStr.getAt(arrayKey) match {
          case Some(valueRef: PVar) =>
            valueRef
          case someValue =>
            val result = PVar(someValue)
            parentStr.setAt(arrayKey, result)
            result
        }
      case None =>
        val result = PVar()
        parentStr.setAt(None, result)
        result
    }
  }

  override def assign(pAny: PAny)(implicit ctx: Context) = {
    parentStr.setAt(optArrayKey, pAny)
  }

  override def unset() {
    if (optArrayKey.isDefined) {
      parentStr.unsetAt(optArrayKey.get)
    }
  }
}
