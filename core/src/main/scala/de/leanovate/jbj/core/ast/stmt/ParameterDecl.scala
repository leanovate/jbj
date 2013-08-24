package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.runtime.value.{PVal, NullVal}
import de.leanovate.jbj.core.ast.{Node, Expr}
import de.leanovate.jbj.core.runtime.context.Context

case class ParameterDecl(typeHint: Option[TypeHint], variableName: String, byRef: Boolean, default: Option[Expr])
  extends Node {

  def defaultVal(implicit ctx: Context): PVal = default.map(_.eval.asVal).getOrElse(NullVal)
}
