/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.NamespaceName
import de.leanovate.jbj.core.runtime.value._
import de.leanovate.jbj.core.runtime.context.{FunctionLikeContext, Context}
import de.leanovate.jbj.core.runtime.exception.CatchableFatalError
import scala.Some
import de.leanovate.jbj.core.runtime.value.IntegerVal

sealed trait TypeHint {
  def check(pVar: PVar, index: Int)(implicit ctx: Context)
}

object TypeHint {
  def displayType(pVal: PVal) = pVal match {
    case NullVal => "null"
    case bool: BooleanVal => "boolean"
    case int: IntegerVal => "integer"
    case double: DoubleVal => "double"
    case str: StringVal => "string"
    case arr: ArrayVal => "array"
    case obj: ObjectVal =>
      "instance of %s".format(obj.pClass.name.toString)
  }
}

object ArrayTypeHint extends TypeHint {
  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {
    pVar.value match {
      case arr: ArrayVal =>
      case NullVal =>
      case pVal =>
        ctx match {
          case methodCtx: FunctionLikeContext =>
            CatchableFatalError("Argument %d passed to %s must be of the type array, %s given, called in %s on line %d and defined".
              format(index + 1, methodCtx.functionSignature, TypeHint.displayType(pVal),
              methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
        }
    }
  }
}

object CallableTypeHint extends TypeHint {
  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {
  }
}

case class ClassTypeHint(className: NamespaceName) extends TypeHint {
  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {
    ctx.global.findInterfaceOrClass(className, autoload = false) match {
      case Some(Left(pInterface)) =>
        pVar.value match {
          case obj: ObjectVal if obj.instanceOf(pInterface) =>
          case NullVal =>
          case pVal =>
            ctx match {
              case methodCtx: FunctionLikeContext =>
                CatchableFatalError("Argument %d passed to %s must implement interface %s, %s given, called in %s on line %d and defined".
                  format(index + 1, methodCtx.functionSignature,
                  pInterface.name.toString, TypeHint.displayType(pVal),
                  methodCtx.callerContext.currentPosition.fileName,  methodCtx.callerContext.currentPosition.line))
            }
        }
      case Some(Right(pClass)) =>
        pVar.value match {
          case obj: ObjectVal if obj.instanceOf(pClass) =>
          case NullVal =>
          case pVal =>
            ctx match {
              case methodCtx: FunctionLikeContext =>
                CatchableFatalError("Argument %d passed to %s must be an instance of %s, %s given, called in %s on line %d and defined".
                  format(index + 1, methodCtx.functionSignature,
                  pClass.name.toString, TypeHint.displayType(pVal),
                  methodCtx.callerContext.currentPosition.fileName,  methodCtx.callerContext.currentPosition.line))
            }
        }
      case _ =>
        pVar.value match {
          case NullVal =>
          case pVal =>
            ctx match {
              case methodCtx: FunctionLikeContext =>
                CatchableFatalError("Argument %d passed to %s must be an instance of %s, %s given, called in %s on line %d and defined".
                  format(index + 1, methodCtx.functionSignature,
                  className.toString, TypeHint.displayType(pVal),
                  methodCtx.callerContext.currentPosition.fileName,  methodCtx.callerContext.currentPosition.line))
            }
        }
    }
  }
}
