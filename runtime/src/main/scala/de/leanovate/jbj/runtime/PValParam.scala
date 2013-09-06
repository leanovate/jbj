package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class PValParam(pVal: PVal)(implicit ctx: Context) extends PParam {
  override def byRef = {
    throw new FatalErrorJbjException("Only variables can be passed by reference")
  }

  override def byVal = pVal
}
