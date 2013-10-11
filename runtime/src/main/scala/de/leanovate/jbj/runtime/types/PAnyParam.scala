package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value.{PAny, PVar, PVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

class PValParam(pVal: PVal)(implicit ctx: Context) extends PParam {
  override def hasRef = false

  override def byRef = None

  override def byVal = pVal
}

class PVarParam(pVar: PVar)(implicit ctx: Context) extends PParam {
  override def hasRef = true

  override def byRef = Some(pVar)

  override def byVal = pVar.value
}

object PAnyParam {
  def apply(pAny: PAny)(implicit ctx: Context) = pAny match {
    case pVar: PVar =>new PVarParam(pVar)
    case pVal: PVal => new PValParam(pVal)
  }

  def apply(pVal: PVal)(implicit ctx: Context) = new PValParam(pVal)

  def apply(pVar: PVar)(implicit ctx: Context) = new PVarParam(pVar)
}
