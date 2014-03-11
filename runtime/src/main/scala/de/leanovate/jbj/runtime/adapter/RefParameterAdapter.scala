/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{NullVal, PVar}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class RefParameterAdapter(parameterIdx: Int) extends ParameterAdapter[PVar] {
  def requiredCount = 1

  def adapt(parameters: List[PParam], strict: Boolean,
            missingErrorHandler: () => Unit,
            conversionErrorHandler: (String, String, Int) => Unit)(implicit ctx: Context) =
    parameters match {
      case head :: tail =>
        val pVar = head.byRef match {
          case Some(pVar: PVar) => pVar
          case Some(pAny) =>
            ctx.log.strict("Only variables should be passed by reference")
            pAny.asVar
          case None =>
            throw new FatalErrorJbjException("Only variables can be passed by reference")
        }
        (pVar, tail)
      case Nil =>
        missingErrorHandler()
        (PVar(), Nil)
    }
}
