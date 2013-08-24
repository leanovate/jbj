package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.value._
import scala.Some
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.Context

case class IndexReferableExpr(reference: ReferableExpr, indexExpr: Option[Expr]) extends ReferableExpr {
  override def evalRef(implicit ctx: Context) = new Reference {
    val parentRef = reference.evalRef
    val optArrayKey = indexExpr.map(_.eval.asVal)

    def isDefined = !asVal.isNull

    def asVal = {
      if (optArrayKey.isEmpty)
        NullVal
      else
        parentRef.asVal match {
          case array: ArrayLike =>
            val result = array.getAt(optArrayKey.get)
            result.map(_.asVal).getOrElse(NullVal)
          case _ => NullVal
        }
    }

    def asVar = {
      optParent.map {
        array =>
          optArrayKey match {
            case Some(arrayKey) =>
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
        ctx.log.warn("Cannot use a scalar value as an array")
        PVar()
      }
    }

    def assign(pAny: PAny) = {
      optParent.map {
        array =>
          optArrayKey match {
            case Some(arrayKey) =>
              array.getAt(arrayKey) match {
                case Some(pVar: PVar) if pAny.isInstanceOf[PVal] =>
                  pVar.value = pAny.asInstanceOf[PVal]
                case _ =>
                  array.setAt(optArrayKey, pAny)
              }
            case None =>
              array.setAt(optArrayKey, pAny)
          }
      }.getOrElse {
        ctx.log.warn("Cannot use a scalar value as an array")
      }
      pAny
    }

    def unset() {
      if (optParent.isDefined && optArrayKey.isDefined) {
        optParent.get.unsetAt(optArrayKey.get)
      }
    }

    private def optParent = parentRef.asVal match {
      case array: ArrayLike => Some(array)
      case NullVal =>
        val array = ArrayVal()
        parentRef.asVar.asVar.value = array
        Some(array)
      case _ =>
        None
    }

  }

  override def eval(implicit ctx: Context) = {
    if (indexExpr.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")
    reference.eval.asVal match {
      case array: ArrayLike =>
        val arrayKey = indexExpr.get.eval.asVal
        val result = array.getAt(arrayKey)
        if (!result.isDefined) {
          arrayKey match {
            case NumericVal(idx) =>
              ctx.log.notice("Undefined offset: %d".format(idx.toLong))
            case idx =>
              ctx.log.notice("Undefined index: %s".format(idx.toStr.asString))
          }
        }
        result.map(_.asVal).getOrElse(NullVal)
      case _ =>
        NullVal
    }
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    reference.dump(out, ident + "  ")
    indexExpr.map(_.dump(out, ident + "  ")).getOrElse {
      out.println(ident + "  <empty>")
    }
  }
}
