/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.context.Context

case class BreakStmt(depth: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = BreakExecResult(depth.map(_.eval.asVal.toInteger.asLong).getOrElse(1))
}
