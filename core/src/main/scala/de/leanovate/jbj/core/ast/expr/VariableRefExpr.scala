/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, RefExpr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.VariableReference._

case class VariableRefExpr(variableName: Name) extends RefExpr {
  override def eval(implicit ctx: Context) = evalRef.byVal

  override def evalRef(implicit ctx: Context) = $(variableName.evalName)

  override def phpStr = "$" + variableName.phpStr
}
