package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import de.leanovate.jbj.runtime.annotations.GlobalFunction

object OutputFunctions extends WrappedFunctions {

  @GlobalFunction
  def var_dump(values: ValueOrRef*)(implicit ctx: Context, position: NodePosition) {
    if (values.isEmpty)
      ctx.log.warn(position, "var_dump() expects at least 1 parameter, 0 given")

    def dump(valueOfRef: ValueOrRef, ident: String) {
      val (value, isRef) = valueOfRef match {
        case valueRef: ValueRef => valueRef.value -> "&"
        case value: Value => value -> ""
      }
      value match {
        case ArrayVal(keyValues) =>
          val nextIdent = ident + "  "
          ctx.out.println("%sarray(%d) {".format(ident, keyValues.size))
          keyValues.foreach {
            case (IntArrayKey(key), v) =>
              ctx.out.println("%s[%d]=>".format(nextIdent, key))
              dump(v, nextIdent)
            case (StringArrayKey(key), v) =>
              ctx.out.println( """%s["%s"]=>""".format(nextIdent, key))
              dump(v, nextIdent)
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
            case (IntArrayKey(key), v) =>
              ctx.out.println("%s[%d]=>".format(nextIdent, key))
              dump(v, nextIdent)
            case (StringArrayKey(key), v) =>
              ctx.out.println( """%s["%s"]=>""".format(nextIdent, key))
              dump(v, nextIdent)
          }
          ctx.out.println("%s}".format(ident))
        case str: StringVal =>
          ctx.out.println( """%sstring(%s) "%s"""".format(ident, str.chars.length, str.asString))
      }
    }

    values.foreach(dump(_, ""))
  }

  @GlobalFunction
  def print_r(value: Value, ret: Option[Boolean])(implicit ctx: Context, position: NodePosition): Value = {
    def dump(value: ValueOrRef): List[String] = {
      value match {
        case ArrayVal(keyValues) =>
          "Array" :: "(" :: keyValues.flatMap {
            case (IntArrayKey(key), v) =>
              val lines = dump(v)
              "    [%d] => %s".format(key, lines.head) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map("        " + _) ::: "" :: Nil)
            case (StringArrayKey(key), v) =>
              val lines = dump(v)
              """    [%s] => %s""".format(key, lines.head) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map("        " + _) ::: "" :: Nil)
          }.toList ::: ")" :: Nil
        case ObjectVal(pClass, _, keyValues) =>
          "%s Object".format(pClass.name.toString) :: "(" :: keyValues.flatMap {
            case (IntArrayKey(key), v) =>
              val lines = dump(v)
              "    [%d] => %s".format(key, lines.head) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map("        " + _) ::: "" :: Nil)
            case (StringArrayKey(key), v) =>
              val lines = dump(v)
              """    [%s] => %s""".format(key, lines.head) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map("        " + _) ::: "" :: Nil)
          }.toList ::: ")" :: Nil
        case v => v.toOutput :: Nil
      }
    }

    if (ret.getOrElse(false))
      StringVal(dump(value).mkString("\n") + "\n")
    else {
      dump(value).foreach(ctx.out.println)
      BooleanVal.TRUE
    }
  }
}
