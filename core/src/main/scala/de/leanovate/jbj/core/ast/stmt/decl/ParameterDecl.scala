/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.runtime.value.{PVal, NullVal}
import de.leanovate.jbj.core.ast.{Node, Expr}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.ast.stmt.decl.TypeHint

case class ParameterDecl(typeHint: Option[TypeHint], variableName: String, byRef: Boolean, default: Option[Expr])
  extends Node {

  def defaultVal(implicit ctx: Context): PVal = default.map(_.eval.asVal).getOrElse(NullVal)
}
