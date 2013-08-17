package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NodePosition, Expr, ReferableExpr}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import scala.Some
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class IndexReferableExpr(reference: ReferableExpr, indexExpr: Option[Expr]) extends ReferableExpr {

  override def position_=(pos: NodePosition) {
    super.position_=(pos)

    reference.position = pos
  }

  override def isDefined(implicit ctx: Context) = {
    if (reference.isDefined) {
      indexExpr.exists {
        expr =>
          reference.eval match {
            case array: ArrayLike => array.getAt(expr.eval).isDefined
            case _ => false
          }
      }
    } else {
      false
    }
  }

  override def eval(implicit ctx: Context) = {
    if (indexExpr.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")
    reference.eval match {
      case array: ArrayLike =>
        val arrayKey = indexExpr.get.eval
        val result = array.getAt(indexExpr.get.eval)
        if (!result.isDefined) {
          arrayKey match {
            case NumericVal(idx) =>
              ctx.log.notice(position, "Undefined offset: %d".format(idx.toLong))
            case idx =>
              ctx.log.notice(position, "Undefined index: %s".format(idx.toStr.asString))
          }
        }
        result.map(_.value).getOrElse(NullVal)
      case _ =>
        NullVal
    }
  }

  override def evalVar(implicit ctx: Context) = {
    parentArray.map {
      array =>
        indexExpr match {
          case Some(expr) =>
            val arrayKey = expr.eval
            array.getAt(arrayKey) match {
              case Some(valueRef: PVar) =>
                valueRef
              case someValue =>
                val result = PVar(someValue)
                array.setAt(arrayKey, result)
                result
            }
          case None =>
            val result = PVar()
            array.setAt(None, result)
            result
        }
    }.getOrElse {
      ctx.log.warn(position, "Cannot use a scalar value as an array")
      PVar()
    }
  }

  override def assignVar(valueOrRef: PAny)(implicit ctx: Context) {
    parentArray.map {
      array =>
        array.setAt(indexExpr.map(_.eval), valueOrRef)
    }.getOrElse {
      ctx.log.warn(position, "Cannot use a scalar value as an array")
    }
  }

  override def unsetVar(implicit ctx: Context) {
    if (indexExpr.isDefined) {
      reference.eval match {
        case array: ArrayLike => array.getAt(indexExpr.get.eval).isDefined
        case _ =>
      }
    }
  }

  private def parentArray(implicit ctx: Context) = if (!reference.isDefined) {
    val array = ArrayVal()
    reference.assignVar(array)
    Some(array)
  } else {
    reference.eval.value match {
      case array: ArrayLike =>
        Some(array)
      case NullVal =>
        val array = ArrayVal()
        reference.assignVar(array)
        Some(array)
      case _ =>
        None
    }
  }

  private def optArrayKey(implicit ctx: Context) = indexExpr.flatMap {
    expr =>
      ArrayKey(expr.eval)
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    reference.dump(out, ident + "  ")
    indexExpr.map(_.dump(out, ident + "  ")).getOrElse {
      out.println(ident + "  <empty>")
    }
  }
}
