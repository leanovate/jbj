/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PArrayAccess
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

class ObjectDimReference(arrayAccess: PArrayAccess, optArrayKey: Option[PVal])(implicit ctx: Context) extends Reference {
  def isConstant = false

  override def isDefined = {
    if (optArrayKey.isEmpty)
      false
    else
      arrayAccess.offsetExists(optArrayKey.get)
  }

  override def byVal = {
    if (optArrayKey.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")
    else
      arrayAccess.offsetGet(optArrayKey.get).asVal
  }

  override def byVar = {
    arrayAccess.offsetGet(optArrayKey.get).asVar
  }

  override def value_=(pAny: PAny)(implicit ctx: Context) = {
    pAny match {
      case pVar: PVar =>
        checkIndirect
        throw new FatalErrorJbjException("Cannot assign by reference to overloaded object")
      case pVal: PVal =>
        optArrayKey match {
          case Some(arrayKey) =>
            arrayAccess.offsetSet(arrayKey, pVal)
          case None =>
            arrayAccess.offsetSet(NullVal, pVal)
        }
    }
    pAny
  }

  override def unset()(implicit ctx: Context) {
    if (optArrayKey.isDefined) {
      arrayAccess.offsetUnset(optArrayKey.get)
    }
  }

  override def checkIndirect = {
    if (arrayAccess.offsetGetIsReturnByRef)
      true
    else {
      ctx.log.notice("Indirect modification of overloaded element of %s has no effect".format(arrayAccess.pClass.name.toString))
      false
    }
  }

  override def dim(optKey: Option[PVal] = None)(implicit ctx: Context) = byVal.concrete match {
    case obj: ObjectVal if obj.instanceOf(PArrayAccess) =>
      new ObjectDimReference(PArrayAccess.cast(obj), optKey)
    case _ =>
      new ArrayDimReference(this, optKey)
  }
}
