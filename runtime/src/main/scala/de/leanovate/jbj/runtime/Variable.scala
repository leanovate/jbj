/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny, NullVal}
import de.leanovate.jbj.runtime.context.Context

object Variable {
  def $(name: String)(implicit ctx:Context): Reference = new Reference {
    def isConstant = false

    def isDefined = ctx.findVariable(name).exists(!_.value.isNull)

    def byVal = ctx.findVariable(name).map(_.asLazyVal).getOrElse {
      ctx.log.notice("Undefined variable: %s".format(name))
      NullVal
    }

    def byVar = ctx.findOrDefineVariable(name)

    def assign(pAny: PAny)(implicit ctx: Context): PAny = {
      pAny match {
        case pVar: PVar =>
          ctx.defineVariable(name, pVar)
        case pVal: PVal =>
          ctx.findOrDefineVariable(name).value = pVal
      }
      pAny
    }

    def unset() {
      ctx.undefineVariable(name)
    }
  }

}
