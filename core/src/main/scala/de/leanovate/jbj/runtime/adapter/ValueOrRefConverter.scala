package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PAnyVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{ReferableExpr, Expr}

object ValueOrRefConverter extends Converter[PAny, PAnyVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) =  expr.eval

  override def toScala(value: PAnyVal)(implicit ctx: Context) = value

  override def toJbj(valueOrRef: PAny)(implicit ctx: Context) = valueOrRef.value
}
