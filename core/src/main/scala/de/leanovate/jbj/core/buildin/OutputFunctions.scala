/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

object OutputFunctions {

  @GlobalFunction
  def var_dump(values: PAny*)(implicit ctx: Context) {
    if (values.isEmpty)
      ctx.log.warn("var_dump() expects at least 1 parameter, 0 given")
    if (ctx.global.isOutputBufferingCallback)
      throw new FatalErrorJbjException("var_dump(): Cannot use output buffering in output buffering display handlers")

    def dump(stack: List[PAny], ident: String) {
      val (value, isRef) = stack.head match {
        case valueRef: PVar if valueRef.refCount > 1 => valueRef.value -> "&"
        case valueRef: PVar => valueRef.value -> ""
        case value: PVal => value -> ""
      }
      value match {
        case ArrayVal(keyValues) =>
          val nextIdent = ident + "  "
          ctx.out.println("%s%sarray(%d) {".format(ident, isRef, keyValues.size))
          keyValues.foreach {
            case (IntegerVal(key), v) =>
              ctx.out.println("%s[%d]=>".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
            case (StringVal(key), v) =>
              ctx.out.println( """%s["%s"]=>""".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
          }
          ctx.out.println("%s}".format(ident))
        case BooleanVal(bool) =>
          ctx.out.println("%s%sbool(%s)".format(ident, isRef, if (bool) "true" else "false"))
        case d: DoubleVal =>
          ctx.out.println( """%s%sfloat(%s)""".format(ident, isRef, d.toOutput))
        case IntegerVal(i) =>
          ctx.out.println( """%s%sint(%d)""".format(ident, isRef, i))
        case NullVal =>
          ctx.out.println("%sNULL".format(ident))
        case ObjectVal(pClass, instanceNum, keyValues) =>
          val nextIdent = ident + "  "
          ctx.out.println("%sobject(%s)#%d (%d) {".format(ident, pClass.name.toString, instanceNum, keyValues.size))
          keyValues.foreach {
            case (ObjectPropertyKey.IntKey(key), v) =>
              ctx.out.println("%s[%d]=>".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
            case (ObjectPropertyKey.PublicKey(key), v) =>
              ctx.out.println( """%s["%s"]=>""".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
            case (ObjectPropertyKey.ProtectedKey(key), v) =>
              ctx.out.println( """%s["%s":protected]=>""".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
            case (ObjectPropertyKey.PrivateKey(key, className), v) =>
              ctx.out.println( """%s["%s":"%s":private]=>""".format(nextIdent, key, className))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
          }
          ctx.out.println("%s}".format(ident))
        case str: StringVal =>
          ctx.out.println( """%s%sstring(%s) "%s"""".format(ident, isRef, str.chars.length, str.asString))
      }
    }

    values.foreach(v => dump(v :: Nil, ""))
  }

  @GlobalFunction
  def var_export(value: PVal)(implicit ctx: Context) = {
    def dump(stack: List[PAny], ident: String): String = {
      stack.head.asVal match {
        case ArrayVal(keyValues) =>
          val builder = new StringBuilder
          builder ++= "array (\n"
          keyValues.foreach {
            case (_, v) if stack.exists(_.eq(v)) =>
              builder ++= ident
              builder ++= "*RECURSION*\n"
            case (IntegerVal(key), v) =>
              builder ++= ident
              builder ++= " %d => %s,\n".format(key, dump(v :: stack, ident + "  "))
            case (key: StringVal, v) =>
              builder ++= ident
              builder ++= "  %s => %s,\n".format(dump(key :: Nil, ident + "  "), dump(v :: stack, ident + "  "))
          }
          builder ++= ")\n"
          builder.result()
        case str: StringVal =>
          val chs = str.chars
          var idx = 0
          val builder = new StringBuilder
          while (idx < chs.length) {
            val start = idx
            while (idx < chs.length && chs(idx) >= 32 && chs(idx) <= 127) {
              idx += 1
            }
            if (idx > start) {
              if (start > 0)
                builder ++= " . "
              builder ++= "'" + new String(chs.slice(start, idx), "UTF-8") + "'"
            } else if (chs(idx) == 0) {
              if (idx == 0)
                builder ++= "''"
              builder ++= " . \"\\0\""
              idx += 1
            } else {
              if (idx == 0)
                builder ++= "''"
              builder ++= "\"\0x" + Integer.toHexString(chs(idx)) + "\""
              idx += 1
            }
          }
          builder.result()
      }
    }
    ctx.out.print(dump(value :: Nil, ""))
  }

  @GlobalFunction
  def print_r(value: PVal, ret: Option[Boolean])(implicit ctx: Context): PVal = {
    if (ctx.global.isOutputBufferingCallback)
      throw new FatalErrorJbjException("print_r(): Cannot use output buffering in output buffering display handlers")

    def dump(stack: List[PAny], ident: String): String = {
      stack.head.asVal match {
        case ArrayVal(keyValues) =>
          val builder = new StringBuilder
          builder ++= "Array\n"
          builder ++= ident
          builder ++= "(\n"
          keyValues.foreach {
            case (_, v) if stack.exists(_.eq(v)) =>
              builder ++= ident
              builder ++= "*RECURSION*\n"
            case (IntegerVal(key), v) =>
              builder ++= ident
              builder ++= "    [%d] => %s\n".format(key, dump(v :: stack, ident + "        "))
            case (StringVal(key), v) =>
              builder ++= ident
              builder ++= "    [%s] => %s\n".format(key, dump(v :: stack, ident + "        "))
          }
          builder ++= ident
          builder ++= ")\n"
          builder.result()
        case ObjectVal(pClass, _, keyValues) =>
          val builder = new StringBuilder
          builder ++= "%s Object\n".format(pClass.name.toString)
          builder ++= ident
          builder ++= "(\n"
          keyValues.foreach {
            case (_, v) if stack.exists(_.eq(v)) =>
              builder ++= ident
              builder ++= "*RECURSION*\n"
            case (ObjectPropertyKey.IntKey(key), v) =>
              builder ++= ident
              builder ++= "    [%d] => %s\n".format(key, dump(v :: stack, ident + "        "))
            case (key, v) =>
              builder ++= ident
              builder ++= "    [%s] => %s\n".format(key.name, dump(v :: stack, ident + "        "))
          }
          builder ++= ident
          builder ++= ")\n"
          builder.result()
        case v => v.toOutput
      }
    }

    if (ret.getOrElse(false))
      StringVal(dump(value :: Nil, ""))
    else {
      ctx.out.print(dump(value :: Nil, ""))
      BooleanVal.TRUE
    }
  }
}
