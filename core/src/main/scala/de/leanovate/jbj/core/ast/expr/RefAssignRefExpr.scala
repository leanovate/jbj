/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.RefExpr
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.{PAny, PVar}
import de.leanovate.jbj.runtime.context.Context

case class RefAssignRefExpr(reference: RefExpr, otherReferable: RefExpr) extends RefExpr {
  override def eval(implicit ctx: Context) = evalRef.byVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val resultRef = reference.evalRef

    val result = {
      val otherRef = otherReferable.evalRef
      if (otherRef.isConstant) {
        ctx.log.strict("Only variables should be assigned by reference")
        resultRef.assign(otherRef.byVal)
      } else {
        resultRef.assign(otherRef.byVar)
      }
    }

    def isConstant = !result.isInstanceOf[PVar]

    def isDefined = !byVal.isNull

    def byVal = result.asVal

    def byVar = result.asVar

    def assign(pAny: PAny)(implicit ctx: Context) = resultRef.assign(pAny)

    def unset() {
      resultRef.unset()
    }
  }
}
