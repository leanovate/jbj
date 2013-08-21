package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.context.Context

object PAnyConverter extends Converter[PAny, PAny] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) =  expr.evalOld

  override def toScala(value: PAny)(implicit ctx: Context) = value

  override def toJbj(valueOrRef: PAny)(implicit ctx: Context) = valueOrRef
}
