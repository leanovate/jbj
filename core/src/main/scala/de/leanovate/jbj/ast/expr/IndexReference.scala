package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import scala.Some

case class IndexReference(reference: Reference, indexExpr: Option[Expr]) extends Reference {

  override def isDefined(implicit ctx: Context) = {
    if (reference.isDefined) {
      optArrayKey.exists {
        arrayKey =>
          val array = reference.eval
          array.getAt(arrayKey).isDefined
      }
    } else {
      false
    }
  }

  override def eval(implicit ctx: Context) = {
    optArrayKey.flatMap {
      arrayKey =>
        val array = reference.eval
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
    }.getOrElse(NullVal)
  }

  override def evalRef(implicit ctx: Context) = {
    val optArray: Option[ArrayVal] = if (!reference.isDefined) {
      val array = ArrayVal()
      reference.assignRef(array)
      Some(array)
    } else {
      reference.eval.value match {
        case array: ArrayVal =>
          Some(array)
        case NullVal =>
          val array = ArrayVal()
          reference.assignRef(array)
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
              case Some(valueRef: ValueRef) =>
                valueRef
              case someValue =>
                val result = ValueRef(someValue)
                array.setAt(Some(key), result)
                result
            }
          case None =>
            val result = ValueRef()
            array.setAt(None, result)
            result
        }
    }.getOrElse {
      ctx.log.warn(position, "Cannot use a scalar value as an array")
      ValueRef()
    }
  }

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
    if (!reference.isDefined) {
      val array = ArrayVal()
      reference.assignRef(array)
      array.setAt(optArrayKey, valueOrRef)
    } else {
      reference.eval.value match {
        case array: ArrayVal =>
          array.setAt(optArrayKey, valueOrRef)
        case NullVal =>
          val array = ArrayVal()
          reference.assignRef(array)
          array.setAt(optArrayKey, valueOrRef)
        case v =>
          ctx.log.warn(position, "Cannot use a scalar value as an array")
      }
    }
  }

  private def optArrayKey(implicit ctx: Context) = indexExpr.flatMap {
    expr =>
      ArrayKey(expr.eval)
  }
}
