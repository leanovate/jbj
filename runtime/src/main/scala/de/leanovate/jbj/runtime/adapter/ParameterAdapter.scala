/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

trait ParameterAdapter[T] {
  def requiredCount: Int

  def adapt(parameters: List[Expr])(implicit ctx: Context): Option[(T, List[Expr])]
}
