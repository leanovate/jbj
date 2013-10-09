/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.Stmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.SuccessExecResult

case class HaltCompilerStmt() extends Stmt {
  override def exec(implicit ctx: Context) = {
    SuccessExecResult
  }
}