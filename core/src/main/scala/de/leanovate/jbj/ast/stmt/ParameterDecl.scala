package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.runtime.value.NullVal
import de.leanovate.jbj.ast.Expr

case class ParameterDecl(typeHint: Option[TypeHint], variableName: String, byRef: Boolean, default: Option[Expr]) {
  def defaultVal(implicit ctx: Context): Value = default.map(_.eval).getOrElse(NullVal)
}
