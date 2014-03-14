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
  override def eval(implicit ctx: Context) = evalRef.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val resultRef = reference.evalRef

    val result = {
      val otherRef = otherReferable.evalRef
      if (otherRef.isConstant) {
        ctx.log.strict("Only variables should be assigned by reference")
        resultRef.value_=(otherRef.asVal)
      } else {
        resultRef.value_=(otherRef.asVar)
      }
    }

    override def isConstant = !result.isInstanceOf[PVar]

    override def isDefined = !asVal.isNull

    override def asVal = result.asVal

    override def asVar = result.asVar

    override def value_=(pAny: PAny)(implicit ctx: Context) = resultRef.value_=(pAny)

    override def unset()(implicit ctx: Context) {
      resultRef.unset()
    }
  }

  override def phpStr = reference.phpStr + "=&" + otherReferable.phpStr
}
