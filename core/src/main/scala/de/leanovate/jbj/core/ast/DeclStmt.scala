/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.SuccessExecResult

trait DeclStmt extends Stmt {
  def register(implicit ctx: Context)

  override final def exec(implicit ctx: Context) = {
    register
    SuccessExecResult
  }
}
