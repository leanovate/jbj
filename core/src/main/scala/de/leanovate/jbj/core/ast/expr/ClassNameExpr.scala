/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.context.Context

case class ClassNameExpr(className: Name) extends Expr {
  override def eval(implicit ctx: Context) = StringVal(className.evalName.toString)

  override def phpStr = className.phpStr
}
