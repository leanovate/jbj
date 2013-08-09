package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.{Context, PFunction}
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.ast.NamespaceName
import de.leanovate.jbj.runtime.StringArrayKey
import scala.annotation.tailrec

object OutputFunctions {
  val functions: Seq[PFunction] = Seq(
    new PFunction() {
      def name = NamespaceName(relative = false, "var_dump")

      def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) = {
        var_dump(parameters: _*)(ctx, callerPosition)
        Left(NullVal)
      }
    },
    BuildinFunction2("print_r", {
      case (ctx, callerPosition, Some(value), ret) =>
        print_r(value, ret.exists(_.toBool.value))(ctx, callerPosition)
    })
  )

  def var_dump(values: Value*)(implicit ctx: Context, position: NodePosition) {
    if (values.isEmpty)
      ctx.log.warn(position, "var_dump() expects at least 1 parameter, 0 given")

    def dump(value: Value, ident: String) {
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
          ctx.out.println("%sbool(%s)".format(ident, if (bool) "true" else "false"))
        case d: DoubleVal =>
          ctx.out.println( """%sfloat(%s)""".format(ident, d.toOutput))
        case IntegerVal(i) =>
          ctx.out.println( """%sint(%d)""".format(ident, i))
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

        case StringVal(str) =>
          ctx.out.println( """%sstring(%s) "%s"""".format(ident, str.length, str))
      }
    }

    values.foreach(dump(_, ""))
  }

  def print_r(value: Value, ret: Boolean = false)(implicit ctx: Context, position: NodePosition): Value = {
    def dump(value: Value): List[String] = {
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
        case v => v.toOutput :: Nil
      }
    }

    if (ret)
      StringVal(dump(value).mkString("\n") + "\n")
    else {
      dump(value).foreach(ctx.out.println)
      BooleanVal.TRUE
    }
  }
}
