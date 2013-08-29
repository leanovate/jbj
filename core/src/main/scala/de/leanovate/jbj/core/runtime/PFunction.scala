/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime

import de.leanovate.jbj.core.ast.{Expr, NamespaceName}
import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.context.Context

trait PFunction {
  def name: NamespaceName

  def call(parameters: List[Expr])(implicit callerCtx: Context): PAny
}
