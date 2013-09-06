/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.runtime.value.{PVal, NullVal}
import de.leanovate.jbj.core.ast.{Node, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.ast.expr.value.ConstGetExpr

case class ParameterDecl(typeHint: Option[TypeHint], variableName: String, byRef: Boolean, default: Option[Expr])
  extends Node {

  def defaultVal(implicit ctx: Context): Option[PVal] = default.map(_.eval.asVal)

  def check(implicit ctx: Context) {
    if (typeHint.isDefined && typeHint.get.isInstanceOf[ClassTypeHint] && default.isDefined && (!default.get.isInstanceOf[ConstGetExpr] ||
      default.get.asInstanceOf[ConstGetExpr].constName.toString.toLowerCase != "null")) {
      throw new FatalErrorJbjException("Default value for parameters with a class type hint can only be NULL")
    }
  }
}
