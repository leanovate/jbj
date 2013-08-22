package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.runtime.value.{PVal, NullVal}
import de.leanovate.jbj.ast.{Node, Expr}
import de.leanovate.jbj.runtime.context.Context

case class ParameterDecl(typeHint: Option[TypeHint], variableName: String, byRef: Boolean, default: Option[Expr])
  extends Node {

  def defaultVal(implicit ctx: Context): PVal = default.map(_.evalOld).getOrElse(NullVal)
}
