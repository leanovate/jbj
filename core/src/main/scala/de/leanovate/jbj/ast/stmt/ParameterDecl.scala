package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{PVal, NullVal}
import de.leanovate.jbj.ast.Expr

case class ParameterDecl(typeHint: Option[TypeHint], variableName: String, byRef: Boolean, default: Option[Expr]) {
  def defaultVal(implicit ctx: Context): PVal = default.map(_.evalOld).getOrElse(NullVal)
}
