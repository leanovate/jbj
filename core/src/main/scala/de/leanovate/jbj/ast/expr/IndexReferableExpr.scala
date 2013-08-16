package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NodePosition, Expr, ReferableExpr}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import scala.Some
import java.io.PrintStream

case class IndexReferableExpr(reference: ReferableExpr, indexExpr: Option[Expr]) extends ReferableExpr {

  override def position_=(pos: NodePosition) {
    super.position_=(pos)

    reference.position = pos
  }

  override def isDefined(implicit ctx: Context) = {
    if (reference.isDefined) {
      optArrayKey.exists {
        arrayKey =>
          reference.eval match {
            case array: ArrayLike => array.getAt(arrayKey).isDefined
            case _ => false
          }
      }
    } else {
      false
    }
  }

  override def eval(implicit ctx: Context) = {
    optArrayKey.flatMap {
      arrayKey =>
        reference.eval match {
          case array: ArrayLike =>
            val result = array.getAt(arrayKey)
            if (!result.isDefined) {
              arrayKey match {
                case IntArrayKey(idx) =>
                  ctx.log.notice(position, "Undefined offset: %d".format(idx))
                case StringArrayKey(idx) =>
                  ctx.log.notice(position, "Undefined index: %s".format(idx))
              }
            }
            result.map(_.value)
          case _ =>
            Some(NullVal)
        }
    }.getOrElse(NullVal)
  }

  override def evalVar(implicit ctx: Context) = {
    val optArray: Option[ArrayLike] = if (!reference.isDefined) {
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

    optArray.map {
      array =>
        optArrayKey match {
          case Some(key) =>
            array.getAt(key) match {
              case Some(valueRef: PVar) =>
                valueRef
              case someValue =>
                val result = PVar(someValue)
                array.setAt(Some(key), result)
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
    if (!reference.isDefined) {
      val array = ArrayVal()
      reference.assignVar(array)
      array.setAt(optArrayKey, valueOrRef)
    } else {
      reference.eval.value match {
        case array: ArrayLike =>
          array.setAt(optArrayKey, valueOrRef)
        case NullVal =>
          val array = ArrayVal()
          reference.assignVar(array)
          array.setAt(optArrayKey, valueOrRef)
        case v =>
          ctx.log.warn(position, "Cannot use a scalar value as an array")
      }
    }
  }

  override def unsetVar(implicit ctx:Context) {
    optArrayKey.foreach {
      arrayKey =>
        reference.eval match {
          case array: ArrayLike => array.getAt(arrayKey).isDefined
          case _ => false
        }
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
