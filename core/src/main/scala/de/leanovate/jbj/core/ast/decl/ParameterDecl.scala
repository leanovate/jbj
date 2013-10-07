/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.core.ast.{Node, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.ast.expr.value.ConstGetExpr
import de.leanovate.jbj.runtime.types.{ClassTypeHint, TypeHint, PParamDef}

case class ParameterDecl(typeHint: Option[TypeHint], name: String, byRef: Boolean, defaultExpr: Option[Expr])
  extends Node with PParamDef {

  override def default = defaultExpr.map {
    expr => {
      ctx => expr.eval(ctx)
    }
  }

  def check(implicit ctx: Context) {
    if (typeHint.isDefined && typeHint.get.isInstanceOf[ClassTypeHint] && defaultExpr.isDefined && (!defaultExpr.get.isInstanceOf[ConstGetExpr] ||
      defaultExpr.get.asInstanceOf[ConstGetExpr].constName.toString.toLowerCase != "null")) {
      throw new FatalErrorJbjException("Default value for parameters with a class type hint can only be NULL")
    }
  }
}
