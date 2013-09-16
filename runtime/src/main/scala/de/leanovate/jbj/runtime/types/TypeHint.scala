/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.{FunctionLikeContext, Context}
import de.leanovate.jbj.runtime.exception.CatchableFatalError
import scala.Some
import de.leanovate.jbj.runtime.NamespaceName

sealed trait TypeHint {
  def display: String

  def checkEmpty(index: Int)(implicit ctx: Context)

  def check(pVar: PVar, index: Int)(implicit ctx: Context)

  def isCompatible(other: TypeHint): Boolean
}

object TypeHint {
}

object ArrayTypeHint extends TypeHint {
  def display: String = "array"

  override def checkEmpty(index: Int)(implicit ctx: Context) {
    ctx match {
      case methodCtx: FunctionLikeContext =>
        CatchableFatalError("Argument %d passed to %s must be of the type array, none given, called in %s on line %d and defined".
          format(index + 1, methodCtx.functionSignature,
          methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
    }
  }

  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {
    pVar.value match {
      case arr: ArrayVal =>
      case NullVal =>
      case pVal =>
        ctx match {
          case methodCtx: FunctionLikeContext =>
            CatchableFatalError("Argument %d passed to %s must be of the type array, %s given, called in %s on line %d and defined".
              format(index + 1, methodCtx.functionSignature, pVal.typeName,
              methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
        }
    }
  }

  override def isCompatible(other: TypeHint): Boolean = {
    other == this
  }
}

object CallableTypeHint extends TypeHint {
  def display: String = "callable"

  override def checkEmpty(index: Int)(implicit ctx: Context) {
  }

  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {
  }

  override def isCompatible(other: TypeHint): Boolean = {
    other == this
  }
}

case class ClassTypeHint(className: NamespaceName) extends TypeHint {
  def display: String = className.toString

  override def checkEmpty(index: Int)(implicit ctx: Context) {
    ctx.global.findInterfaceOrClass(className, autoload = false) match {
      case Some(Left(pInterface)) =>
        ctx match {
          case methodCtx: FunctionLikeContext =>
            CatchableFatalError("Argument %d passed to %s must implement interface %s, none given, called in %s on line %d and defined".
              format(index + 1, methodCtx.functionSignature, pInterface.name.toString,
              methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
        }
      case _ =>
        ctx match {
          case methodCtx: FunctionLikeContext =>
            CatchableFatalError("Argument %d passed to %s must be an instance of %s, none given, called in %s on line %d and defined".
              format(index + 1, methodCtx.functionSignature, className.toString,
              methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
        }
    }
  }

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
                  pInterface.name.toString, pVal.typeName,
                  methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
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
                  pClass.name.toString, pVal.typeName,
                  methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
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
                  className.toString, pVal.typeName,
                  methodCtx.callerContext.currentPosition.fileName, methodCtx.callerContext.currentPosition.line))
            }
        }
    }
  }

  override def isCompatible(other: TypeHint): Boolean = {
    other == this
  }
}
