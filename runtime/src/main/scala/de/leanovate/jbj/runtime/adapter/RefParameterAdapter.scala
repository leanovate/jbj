/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PParam

object RefParameterAdapter extends ParameterAdapter[PVar] {
  def requiredCount = 1

  def adapt(parameters: List[PParam])(implicit ctx: Context) =
    parameters match {
      case head :: tail =>
        val pVar = head.byRef match {
          case pVar: PVar => pVar
          case pAny =>
            ctx.log.strict("Only variables should be passed by reference")
            pAny.asVar
        }
        Some(pVar, tail)
      case Nil => None
    }
}
