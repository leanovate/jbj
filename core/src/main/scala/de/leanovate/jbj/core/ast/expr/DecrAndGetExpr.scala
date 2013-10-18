/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{RefExpr, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.Reference.--

case class DecrAndGetExpr(reference: RefExpr) extends Expr {
  override def eval(implicit ctx: Context) = --(reference.evalRef)

  override def phpStr = "--" + reference.phpStr
}
