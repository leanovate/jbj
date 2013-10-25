/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

object OutputFunctions {
  @GlobalFunction
  def printf(format: String, args: PVal*)(implicit ctx: Context) {
    ctx.out.print(format.format(args.map {
      case DoubleVal(d) => d
      case IntegerVal(v) => v
      case StringVal(str) => str
      case v => v.toStr.asString
    }: _*))
  }

  @GlobalFunction
  def var_dump(values: PAny*)(implicit ctx: Context) {
    if (values.isEmpty)
      ctx.log.warn("var_dump() expects at least 1 parameter, 0 given")
    if (ctx.global.isOutputBufferingCallback)
      throw new FatalErrorJbjException("var_dump(): Cannot use output buffering in output buffering display handlers")

    def dump(stack: List[PAny], ident: String) {
      val (value, isRef) = stack.head match {
        case valueRef: PVar if valueRef.refCount > 1 => valueRef.value.concrete -> "&"
        case valueRef: PVar => valueRef.value.concrete -> ""
        case value: PVal => value.concrete -> ""
      }
      value match {
        case ArrayVal(keyValues) =>
          val nextIdent = ident + "  "
          ctx.out.println(s"${
            ident
          }${isRef}array(${keyValues.size}) {")
          keyValues.foreach {
            case (IntegerVal(key), v) =>
              ctx.out.println(s"$nextIdent[$key]=>")
              if (stack.exists(_.asVal.concrete == v.asVal.concrete))
                ctx.out.println(s"$nextIdent*RECURSION*")
              else
                dump(v :: stack, nextIdent)
            case (StringVal(key), v) =>
              ctx.out.println( s"""$nextIdent["$key"]=>""")
              if (stack.exists(_.asVal.concrete == v.asVal.concrete))
                ctx.out.println(s"$nextIdent*RECURSION*")
              else
                dump(v :: stack, nextIdent)
          }
          ctx.out.println(s"$ident}")
        case BooleanVal(bool) =>
          ctx.out.println(s"$ident${isRef}bool(${if (bool) "true" else "false"})")
        case d: DoubleVal =>
          ctx.out.println( s"""$ident${isRef}float(${d.toOutput})""")
        case IntegerVal(i) =>
          ctx.out.println( s"""$ident${isRef}int($i)""")
        case NullVal =>
          ctx.out.println(s"${ident}NULL")
        case ObjectVal(pClass, instanceNum, keyValues) =>
          val nextIdent = ident + "  "
          ctx.out.println(s"${ident}object(${pClass.name.toString})#$instanceNum (${keyValues.size}) {")
          keyValues.foreach {
            case (ObjectPropertyKey.IntKey(key), v) =>
              ctx.out.println(s"$nextIdent[$key]=>")
              if (stack.exists(_.asVal.concrete == v.asVal.concrete))
                ctx.out.println(s"$nextIdent*RECURSION*")
              else
                dump(v :: stack, nextIdent)
            case (ObjectPropertyKey.PublicKey(key), v) =>
              ctx.out.println( s"""$nextIdent["$key"]=>""")
              if (stack.exists(_.asVal.concrete == v.asVal.concrete))
                ctx.out.println(s"$nextIdent*RECURSION*")
              else
                dump(v :: stack, nextIdent)
            case (ObjectPropertyKey.ProtectedKey(key), v) =>
              ctx.out.println( s"""$nextIdent["$key":protected]=>""")
              if (stack.exists(_.asVal.concrete == v.asVal.concrete))
                ctx.out.println(s"$nextIdent*RECURSION*")
              else
                dump(v :: stack, nextIdent)
            case (ObjectPropertyKey.PrivateKey(key, className), v) =>
              ctx.out.println( s"""$nextIdent["$key":"$className":private]=>""")
              if (stack.exists(_.asVal.concrete == v.asVal.concrete))
                ctx.out.println(s"$nextIdent*RECURSION*")
              else
                dump(v :: stack, nextIdent)
          }
          ctx.out.println(s"$ident}")
        case str: StringVal =>
          ctx.out.println( s"""$ident${isRef}string(${str.chars.length}) "${str.asString}"""" )
      }
    }

    values.foreach(v => dump(v.asVal.concrete :: Nil, ""))
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
        case BooleanVal.TRUE => "true"
        case BooleanVal.FALSE => "false"
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
            case (ObjectPropertyKey.PublicKey(key), v) =>
              builder ++= ident
              builder ++= "    [%s] => %s\n".format(key, dump(v :: stack, ident + "        "))
            case (ObjectPropertyKey.ProtectedKey(key), v) =>
              builder ++= ident
              builder ++= "    [%s:*:protected] => %s\n".format(key, dump(v :: stack, ident + "        "))
            case (ObjectPropertyKey.PrivateKey(key, className), v) =>
              builder ++= ident
              builder ++= "    [%s:%s:private] => %s\n".format(key, className, dump(v :: stack, ident + "        "))
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
