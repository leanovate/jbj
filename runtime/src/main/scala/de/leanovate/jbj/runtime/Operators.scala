package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.value.IntegerVal
import scala.language.implicitConversions

object Operators {
  def $(name: String)(implicit ctx: Context): Reference = new VariableReference(name)

  def p(value: String)(implicit ctx: Context) = StringVal(value)

  def p(value: Int)(implicit ctx: Context) = IntegerVal(value)

  def p(value: Double)(implicit ctx: Context) = DoubleVal(value)

  def lvar(name: String)(implicit ctx: Context): PVar = {
    ctx.findVariable(name).getOrElse {
      val pVar = PVar()

      ctx.defineVariable(name, pVar)
      pVar
    }
  }

  def inline(value: String)(implicit ctx: Context) = ctx.out.print(value)

  def echo(values: PAny*)(implicit ctx: Context) = {
    values.foreach {
      value =>
        ctx.out.print(value.toOutput)
    }
  }

  def print(value: PAny)(implicit ctx: Context): PVal = {
    ctx.out.print(value.toOutput)
    IntegerVal(1)
  }

  implicit def pAny2Boolean(value: PAny): Boolean = value.asVal.toBool.asBoolean

  implicit def pAny2String(value: PAny)(implicit ctx: Context): String = value.asVal.toStr.asString
}
