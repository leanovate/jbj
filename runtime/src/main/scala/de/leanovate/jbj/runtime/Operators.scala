package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.{FunctionContext, Context}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.types.PAnyParam
import scala.language.implicitConversions

object Operators {
  def $(name: String)(implicit ctx: Context): Reference = new VariableReference(name)

  def p(value: PVal) = value

  def p(value: String)(implicit ctx: Context) = StringVal(value)

  def p(value: Int)(implicit ctx: Context) = IntegerVal(value)

  def p(value: Double)(implicit ctx: Context) = DoubleVal(value)

  def f(name: String)(parameters: PAny*)(implicit ctx: Context) =
    ctx.call(NamespaceName(name), parameters.map(PAnyParam.apply))

  def ++(ref: Reference)(implicit ctx: Context): PVal =
    if (ref.checkIndirect) ref.:=(ref.asVal.incr).asVal else ref.asVal.incr

  def --(ref: Reference)(implicit ctx: Context): PVal =
    if (ref.checkIndirect) ref.:=(ref.asVal.decr).asVal else ref.asVal.decr

  def lvar(name: String)(implicit ctx: Context): PVar = {
    ctx.findVariable(name).getOrElse {
      val pVar = PVar()

      ctx.defineVariable(name, pVar)
      pVar
    }
  }

  def lvar(name: String, initial: PVal)(implicit ctx: Context): PVar = {
    ctx.findVariable(name).getOrElse {
      val pVar = PVar(initial)

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

  def map(keyValues: (Option[PVal], PAny)*)(implicit ctx: Context) = ArrayVal(keyValues: _*)

  def array(values: PAny*)(implicit ctx: Context) = ArrayVal(values.map(None -> _): _*)

  def pFor(before: => Unit, cond: => PAny, after: => Unit)(body: => Unit)(implicit ctx: Context) = {
    before
    while (cond.asVal.toBool.asBoolean) {
      body
      after
    }
  }

  def functionCtx(name: String, ctx: Context)(body: Context => PAny): PAny = {
    val funcCtx = FunctionContext(NamespaceName(name), ctx)

    body(funcCtx)
  }

  implicit def pAny2PVal(value: PAny): PVal = value.asVal

  implicit def pAny2Boolean(value: PAny): Boolean = value.asVal.toBool.asBoolean

  implicit def pAny2String(value: PAny)(implicit ctx: Context): String = value.asVal.toStr.asString
}
