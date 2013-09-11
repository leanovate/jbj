/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.PAny

trait BinaryReferableExpr extends ReferableExpr {
  def reference: ReferableExpr

  def expr: Expr

  override def evalRef(implicit ctx: Context): Reference = new Reference {
    val result = eval

    def isConstant = true

    def isDefined = !byVal.isNull

    def byVal = result.asVal

    def byVar = result.asVar

    def assign(pAny: PAny)(implicit ctx:Context) = {
      pAny
    }

    def unset() {
    }
  }
}
