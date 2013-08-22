package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.PVisibility
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context

object OutputFunctions extends WrappedFunctions {

  @GlobalFunction
  def var_dump(values: PAny*)(implicit ctx: Context, position: NodePosition) {
    if (values.isEmpty)
      ctx.log.warn("var_dump() expects at least 1 parameter, 0 given")

    def dump(stack: List[PAny], ident: String) {
      val (value, isRef) = stack.head match {
        case valueRef: PVar => valueRef.value -> "&"
        case value: PVal => value -> ""
      }
      value match {
        case ArrayVal(keyValues) =>
          val nextIdent = ident + "  "
          ctx.out.println("%sarray(%d) {".format(ident, keyValues.size))
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
            case (IntegerVal(key), (visibility, v)) =>
              ctx.out.println("%s[%d]=>".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
            case (StringVal(key), (PVisibility.PUBLIC, v)) =>
              ctx.out.println( """%s["%s"]=>""".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
            case (StringVal(key), (PVisibility.PROTECTED, v)) =>
              ctx.out.println( """%s["%s":protected]=>""".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
            case (StringVal(key), (PVisibility.PRIVATE, v)) =>
              ctx.out.println( """%s["%s":private]=>""".format(nextIdent, key))
              if (stack.exists(_.eq(v)))
                ctx.out.println("%s*RECURSION*".format(nextIdent))
              else
                dump(v :: stack, nextIdent)
          }
          ctx.out.println("%s}".format(ident))
        case str: StringVal =>
          ctx.out.println( """%sstring(%s) "%s"""".format(ident, str.chars.length, str.asString))
      }
    }

    values.foreach(v => dump(v :: Nil, ""))
  }

  @GlobalFunction
  def print_r(value: PVal, ret: Option[Boolean])(implicit ctx: Context, position: NodePosition): PVal = {
    def dump(stack: List[PAny]): List[Option[String]] = {
      stack.head.asVal match {
        case ArrayVal(keyValues) =>
          Some("Array") :: Some("(") :: keyValues.flatMap {
            case (_, v) if stack.exists(_.eq(v)) =>
              Some("*RECURSION*") :: Nil
            case (IntegerVal(key), v) =>
              val lines = dump(v :: stack)
              Some("    [%d] => %s".format(key, lines.head.getOrElse(""))) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map(_.map("        " + _)) ::: None :: Nil)
            case (StringVal(key), v) =>
              val lines = dump(v :: stack)
              Some( """    [%s] => %s""".format(key, lines.head.getOrElse(""))) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map(_.map("        " + _)) ::: None :: Nil)
          }.toList ::: Some(")") :: Nil
        case ObjectVal(pClass, _, keyValues) =>
          Some("%s Object".format(pClass.name.toString)) :: Some("(") :: keyValues.flatMap {
            case (_, v) if stack.exists(_.eq(v)) =>
              Some("*RECURSION*") :: Nil
            case (IntegerVal(key), (visibility, v)) =>
              val lines = dump(v :: stack)
              Some("    [%d] => %s".format(key, lines.head.getOrElse(""))) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map(_.map("        " + _)) ::: None :: Nil)
            case (StringVal(key), (visibility, v)) =>
              val lines = dump(v :: stack)
              Some( """    [%s] => %s""".format(key, lines.head.getOrElse(""))) :: (
                if (lines.tail.isEmpty) Nil else lines.tail.map(_.map("        " + _)) ::: None :: Nil)
          }.toList ::: Some(")") :: Nil
        case v => Some(v.toOutput) :: Nil
      }
    }

    if (ret.getOrElse(false))
      StringVal(dump(value :: Nil).map(_.getOrElse("")).mkString("\n") + "\n")
    else {
      dump(value :: Nil).map(_.getOrElse("")).foreach(ctx.out.println)
      BooleanVal.TRUE
    }
  }
}
