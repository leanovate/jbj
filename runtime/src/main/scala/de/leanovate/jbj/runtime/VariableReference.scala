/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny, NullVal}
import de.leanovate.jbj.runtime.context.{GlobalContext, StaticMethodContext, Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class VariableReference(name: String)(implicit ctx: Context) extends Reference {
  override def isConstant = false

  override def isDefined = ctx.findVariable(name).exists(!_.value.isNull)

  override def byVal = ctx.findVariable(name).map(_.asLazyVal).getOrElse {
    ctx match {
      case StaticMethodContext(_, _, _, false) if name == "this" =>
        throw new FatalErrorJbjException("Using $this when not in object context")
      case _ =>
        ctx.log.notice("Undefined variable: %s".format(name))
        NullVal
    }
  }

  override def byVar = {
    ctx match {
      case StaticMethodContext(_, _, _, false) if name == "this" =>
        throw new FatalErrorJbjException("Using $this when not in object context")
      case _: GlobalContext if name == "this" =>
        throw new FatalErrorJbjException("Using $this when not in object context")
      case _ =>
        ctx.findOrDefineVariable(name)
    }
  }

  override def assign(pAny: PAny)(implicit ctx: Context): PAny = {
    pAny match {
      case pVar: PVar =>
        ctx.defineVariable(name, pVar)
      case pVal: PVal =>
        ctx.findOrDefineVariable(name).value = pVal
    }
    pAny
  }

  override def unset() {
    ctx.undefineVariable(name)
  }
}

object VariableReference {
  def $(name: String)(implicit ctx: Context): Reference = new VariableReference(name)
}
