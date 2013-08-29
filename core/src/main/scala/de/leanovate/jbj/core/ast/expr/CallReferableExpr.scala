/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.ReferableExpr
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

trait CallReferableExpr extends ReferableExpr {
  override def eval(implicit ctx: Context) = call.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = call

    def isDefined = !asVal.isNull

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny)(implicit ctx:Context) = pAny

    def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  def call(implicit ctx: Context): PAny
}
