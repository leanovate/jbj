/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.exception.{FatalErrorJbjException, RuntimeJbjException}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.core.buildin.PException

case class ThrowStmt(expr: Expr) extends Stmt {
  override def exec(implicit ctx: Context) = expr.eval.asVal match {
    case obj: ObjectVal if obj.instanceOf(PException) =>
      throw new RuntimeJbjException(obj)
    case obj: ObjectVal =>
      throw new FatalErrorJbjException("Exceptions must be valid objects derived from the Exception base class")
    case _ =>
      throw new FatalErrorJbjException("Can only throw objects")
  }
}
