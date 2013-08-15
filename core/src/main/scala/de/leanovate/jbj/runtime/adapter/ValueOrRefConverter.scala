package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{Reference, Expr}

object ValueOrRefConverter extends Converter[ValueOrRef, Value] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) =  expr.eval

  override def toScala(value: Value)(implicit ctx: Context) = value

  override def toJbj(valueOrRef: ValueOrRef)(implicit ctx: Context) = valueOrRef.value
}
