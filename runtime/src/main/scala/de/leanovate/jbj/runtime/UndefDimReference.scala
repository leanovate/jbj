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

class UndefDimReference(parentRef: Reference, optArrayKey: Option[PVal])(implicit ctx: Context) extends Reference {
  def isConstant = false

  override def isDefined = false

  override def byVal = {
    if (optArrayKey.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")
    else {
      parentRef.byVal.concrete
      NullVal
    }
  }

  override def byVar = {
    optArrayKey match {
      case Some(arrayKey) if arrayKey.concrete.isInstanceOf[ObjectVal] =>
        ctx.log.warn("Illegal offset type")
        PVar()
      case Some(arrayKey) =>
        createParent.getAt(arrayKey) match {
          case Some(valueRef: PVar) =>
            valueRef
          case someValue =>
            val result = PVar(someValue)
            createParent.setAt(arrayKey, result)
            result
        }
      case None =>
        val result = PVar()
        createParent.setAt(None, result)
        result
    }
  }

  override def assign(pAny: PAny)(implicit ctx: Context) = {
    optArrayKey match {
      case Some(arrayKey) if arrayKey.concrete.isInstanceOf[ObjectVal] =>
        ctx.log.warn("Illegal offset type")
        NullVal
      case Some(arrayKey) =>
        createParent.getAt(arrayKey) match {
          case Some(pVar: PVar) if pAny.isInstanceOf[PVal] =>
            pVar.value = pAny.asInstanceOf[PVal]
          case _ =>
            createParent.setAt(optArrayKey, pAny)
        }
      case None =>
        createParent.setAt(optArrayKey, pAny)
    }
    pAny
  }

  override def unset()(implicit ctx: Context) {
    if (optArrayKey.isDefined) {
      createParent.unsetAt(optArrayKey.get)
    }
  }

  private def createParent: ArrayVal = {
    val array = ArrayVal()
    parentRef.byVar.value = array
    array
  }
}