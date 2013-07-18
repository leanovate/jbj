package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.ast.value.UndefinedVal

case class ParameterDef(variableName: String, byRef: Boolean, default: Option[Expr]) {
  def defaultVal(ctx: Context): Value = default.map(_.eval(ctx)).getOrElse(UndefinedVal)
}
