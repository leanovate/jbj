package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value, ArrayVal, NullVal}
import de.leanovate.jbj.runtime.IntArrayKey
import scala.Some

case class IndexReference(reference: Reference, indexExpr: Option[Expr]) extends Reference {

  override def isDefined(implicit ctx: Context) = {
    if (reference.isDefined) {
      val optArrayKey = indexExpr.flatMap {
        expr =>
          ArrayKey(expr.eval)
      }

      optArrayKey.exists {
        arrayKey =>
          val array = reference.eval
          array.getAt(arrayKey).isDefined
      }
    } else {
      false
    }
  }

  override def evalRef(implicit ctx: Context) = {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval)
    }

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
        result
    }.getOrElse(NullVal)
  }

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval)
    }

    val array = if (reference.isDefined) reference.eval.toArray else ArrayVal()
    reference.assignRef(array)
    array.setAt(optArrayKey, valueOrRef.value)
  }

}
