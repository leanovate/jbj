package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.{PVal, DoubleVal, IntegerVal, StringVal}

object Operators {
  def $(name: String)(implicit ctx: Context): Reference = new VariableReference(name)

  def p(value: String)(implicit ctx: Context) = StringVal(value)

  def p(value: Int)(implicit ctx: Context) = IntegerVal(value)

  def p(value: Double)(implicit ctx: Context) = DoubleVal(value)

  def inline(value: String)(implicit ctx: Context) = ctx.out.print(value)

  def echo(values: PVal*)(implicit ctx: Context) = {
    values.foreach {
      value =>
        ctx.out.print(value.toOutput)
    }
  }

  def print(value: PVal)(implicit ctx: Context): PVal = {
    ctx.out.print(value.toOutput)
    IntegerVal(1)
  }
}
