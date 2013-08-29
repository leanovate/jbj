/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{Name, Stmt}
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.runtime.context.Context

case class GlobalVarDeclAssignStmt(variableNames: List[Name]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    variableNames.foreach {
      variableName =>
        val name = variableName.evalName
        val valueRef = ctx.global.findOrDefineVariable(name)
        ctx.defineVariable(name, valueRef)
    }
    SuccessExecResult
  }
}
