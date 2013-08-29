/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{Stmt, Expr}
import de.leanovate.jbj.core.runtime.ReturnExecResult
import de.leanovate.jbj.core.runtime.context.Context

case class ReturnStmt(expr: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    ReturnExecResult(expr)
  }
}
