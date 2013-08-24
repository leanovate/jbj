package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.PVar
import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.Context

object RefParameterAdapter extends ParameterAdapter[PVar] {
  def requiredCount = 1

  def adapt(parameters: List[Expr])(implicit ctx: Context) =
    parameters match {
      case head :: tail =>
        val pVar = head match {
          case reference: ReferableExpr =>
            reference.evalRef.asVar match {
              case pVar: PVar => pVar
              case pAny =>
                ctx.log.strict("Only variables should be passed by reference")
                pAny.asVar
            }
          case _ =>
            throw new FatalErrorJbjException("Only variables can be passed by reference")
        }
        Some(pVar, tail)
      case Nil => None
    }

}
