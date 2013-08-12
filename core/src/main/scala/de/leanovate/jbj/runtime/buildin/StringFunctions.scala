package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{BooleanVal, StringVal, IntegerVal}
import de.leanovate.jbj.runtime.PFunction

object StringFunctions {
  val functions: Seq[PFunction] = Seq(
    BuildinFunction1("strlen", {
      case (ctx, _, Some(str)) => IntegerVal(str.toStr(ctx).asString(ctx).length)
    }),
    BuildinFunction3("strstr", {
      case (ctx, _, Some(haystack), Some(needle), beforeNeedle) =>
        val needleStr = needle match {
          case str: StringVal => str.asString(ctx)
          case int: IntegerVal => int.asLong.toChar.toString
        }
        val idx = haystack.toStr(ctx).asString(ctx).indexOf(needleStr)
        if (idx < 0) {
          BooleanVal.FALSE
        } else if (beforeNeedle.exists(_.toBool(ctx).asBoolean)) {
          StringVal(haystack.toStr(ctx).asString(ctx).substring(0, idx))(ctx)
        } else {
          StringVal(haystack.toStr(ctx).asString(ctx).substring(idx))(ctx)
        }
    }),
    BuildinFunction1("strtolower", {
      case (ctx, _, Some(str)) => StringVal(str.toStr(ctx).asString(ctx).toLowerCase)(ctx)
    }),
    BuildinFunction1("strtoupper", {
      case (ctx, _, Some(str)) => StringVal(str.toStr(ctx).asString(ctx).toUpperCase)(ctx)
    }),
    BuildinFunction1("bin2hex", {
      case (ctx, _, Some(str)) => {
        val chars = str.toStr(ctx).chars
        val result = new Array[Byte](chars.length * 2)
        for (i <- Range(0, chars.length)) {
          val ch = chars(i)
          result(i * 2) = Character.forDigit(ch >> 4, 16).toByte
          result(i * 2 +1) = Character.forDigit(ch & 0xf, 16).toByte
        }
        StringVal(result)
      }
    })

  )
}
